package com.guicarneirodev.plugins

import com.google.api.core.ApiFuture
import com.google.firebase.cloud.FirestoreClient
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.MultiPartEmail
import java.io.ByteArrayOutputStream
import javax.mail.util.ByteArrayDataSource

suspend fun <T> ApiFuture<T>.await(): T = withContext(Dispatchers.IO) {
    get()
}

suspend fun getPatientData(userId: String, patientId: String): Map<String, Any>? {
    val db = FirestoreClient.getFirestore()
    val patientDoc = db.collection("users").document(userId)
        .collection("patients").document(patientId)
        .get().await()

    if (!patientDoc.exists()) return null

    val patientData = patientDoc.data ?: return null

    val resultsCollection = patientDoc.reference.collection("results")
    val results = resultsCollection.get().await().documents.map { resultDoc ->
        mapOf(
            "created" to (resultDoc.getTimestamp("created")?.toDate()?.toString() ?: ""),
            "name" to (resultDoc.getString("name") ?: ""),
            "value" to (resultDoc.getString("value") ?: "")
        )
    }

    return patientData + mapOf("results" to results)
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondWithPdfReport(patientData: Map<String, Any>) {
    val pdfBytes = generatePdfReport(patientData)

    call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "patient_report.pdf").toString()
    )
    call.respondBytes(pdfBytes, ContentType.Application.Pdf)
}

private fun generatePdfReport(patientData: Map<String, Any>): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val writer = PdfWriter(outputStream)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    document.add(Paragraph("Relatório do paciente"))
    document.add(Paragraph("Nome do paciente: ${patientData["patientName"]}"))
    document.add(Paragraph("Data de avaliação: ${patientData["evaluationDate"]}"))

    document.add(Paragraph("Resultados:"))
    @Suppress("UNCHECKED_CAST")
    val results = patientData["results"] as? List<Map<String, String>> ?: emptyList()
    for (result in results) {
        document.add(Paragraph("  Articulação: ${result["name"]}"))
        document.add(Paragraph("  Valor encontrado: ${result["value"]}"))
        document.add(Paragraph(""))
    }

    document.close()
    return outputStream.toByteArray()
}

private fun sendEmailWithPdf(to: String, pdfBytes: ByteArray) {
    val email = MultiPartEmail()

    email.hostName = "smtp.gmail.com"
    email.setSmtpPort(587)
    email.isStartTLSEnabled = true
    email.setAuthentication("SEU EMAIL", "SUA SENHA")

    email.setFrom("SEU EMAIL")
    email.addTo(to)
    email.subject = "Relatório do paciente"
    email.setMsg("Segue em anexo o relatório do paciente.")

    val attachment = EmailAttachment()
    attachment.disposition = EmailAttachment.ATTACHMENT
    attachment.name = "patient_report.pdf"

    val dataSource = ByteArrayDataSource(pdfBytes, "application/pdf")
    email.attach(dataSource, "patient_report.pdf", "Relatório do paciente")

    try {
        email.send()
        println("Email sent successfully.")
    } catch (e: EmailException) {
        e.printStackTrace()
        println("Failed to send email: ${e.message}")
        throw e
    }
}

fun Application.configureRouting() {
    routing {
        get("/api/users/{userId}/patients/{patientId}/send-pdf") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "User ID is required")
                return@get
            }
            val patientId = call.parameters["patientId"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Patient ID is required")
                return@get
            }
            val email = call.request.queryParameters["email"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Email is required")
                return@get
            }

            val patientData = getPatientData(userId, patientId) ?: run {
                call.respond(HttpStatusCode.NotFound, "Patient not found")
                return@get
            }

            try {
                val pdfBytes = generatePdfReport(patientData)
                sendEmailWithPdf(email, pdfBytes)
                respondWithPdfReport(patientData)
                call.respond(HttpStatusCode.OK, "PDF sent successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to send PDF: ${e.message}")
            }
        }
    }
}
