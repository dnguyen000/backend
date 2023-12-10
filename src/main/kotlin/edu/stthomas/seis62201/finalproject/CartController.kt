package edu.stthomas.seis62201.finalproject


import io.micronaut.context.annotation.Value
import io.micronaut.core.io.ResourceLoader
import io.micronaut.http.MediaType
import io.micronaut.serde.annotation.Serdeable
import com.google.gson.*
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import java.io.BufferedReader
import java.io.File

@Serdeable
data class State(val name: String, val abbrev: String)

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
    @Post("/purchase")
    fun purchase(@Body orderConfirmation: OrderConfirmation): String {
        return "works"
    }

}