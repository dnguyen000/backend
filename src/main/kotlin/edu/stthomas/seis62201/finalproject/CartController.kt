package edu.stthomas.seis62201.finalproject


import io.micronaut.core.io.ResourceLoader
import io.micronaut.http.MediaType
import io.micronaut.serde.annotation.Serdeable
import com.google.gson.*
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import java.io.BufferedReader
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Serdeable
data class State(val name: String, val abbrev: String)

@Serdeable
data class Message(val message: String)

@Introspected
@Serdeable.Deserializable
data class Address(val firstName: String, val lastName: String, val street: String, val zipCode: String, val city: String, val state: String, val phoneNumber: String)

@Introspected
@Serdeable.Deserializable
data class Payment(val ccNumber: String, val ccExpr: String, val ccCVV: String)

@Introspected
@Serdeable.Deserializable
data class Product(val id: Int, val name: String, val qty: Int, val price: Float)

@Serdeable
data class OrderConfirmation(val address: Address, val payment: Payment, val products: List<Product>)

@Controller("/cart")
class CartController(private val resourceLoader: ResourceLoader) {


    @Get
    @Produces(MediaType.APPLICATION_JSON)
    fun index(): List<State> {
        val bufferedReader: BufferedReader = File("src/main/resources/states.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        val gson = Gson()

        return gson.fromJson(inputString, Array<State>::class.java).toList()
    }

    private fun getDate(month: String, year: String, dtf: DateTimeFormatter): LocalDate {
        val convertedDate: LocalDate = LocalDate.parse("""$month/01/$year""", dtf)

        return convertedDate.withDayOfMonth(convertedDate.month.length(convertedDate.isLeapYear))
    }

    @Post("/purchase")
    fun purchase(@Body orderConfirmation: OrderConfirmation): MutableHttpResponse<Message> {
        val expirationMonth = orderConfirmation.payment.ccExpr.subSequence(0, 2) as String
        val expirationYear = orderConfirmation.payment.ccExpr.subSequence(2, 6) as String
        val dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val expirationDate = getDate(expirationMonth, expirationYear, dtf)

        return if ((LocalDate.now().isAfter(expirationDate)) || (orderConfirmation.payment.ccNumber.length != 16)) HttpResponse.badRequest(Message("Invalid card"))
            .contentType(MediaType.APPLICATION_JSON) else HttpResponse.ok(Message("Success"))
            .contentType(MediaType.APPLICATION_JSON)
    }

}
