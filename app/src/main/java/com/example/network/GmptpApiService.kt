package com.example.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Base URL for the FastAPI backend. Use 10.0.2.2 for Android Emulator connecting to local host.
private const val BASE_URL = "http://10.0.2.2:8000/"

// Response & Request Models
data class HealthCheckResponse(
    val status: String,
    val memory_leak_guard: String,
    val slow_network_sync_queue: String,
    val timestamp: String
)

data class PrincipalRegistrationRequest(
    val school_name_en: String,
    val school_name_hi: String,
    val school_code: String,
    val principal_name: String,
    val mobile: String,
    val email: String,
    val state: String,
    val district: String,
    val block: String,
    val is_rural: Boolean,
    val panchayat: String? = "",
    val village: String? = "",
    val nagar_palika: String? = "",
    val ward_number: String? = ""
)

data class PrincipalRegistrationResponse(
    val success: Boolean,
    val message: String,
    val school: Map<String, Any>?
)

data class AiSuvicharRequest(
    val school_id: String,
    val language: String = "both",
    val theme: String = "Perseverance & Wisdom"
)

data class AiSuvicharResponse(
    val quote_hi: String,
    val quote_en: String,
    val author_hi: String,
    val author_en: String,
    val date: String,
    val school_branding: String
)

data class ExamGradeOcrRequest(
    val school_id: String,
    val class_name: String,
    val subject: String,
    val image_base64: String,
    val rubric_total_points: Int = 100
)

data class ExamGradeOcrResponse(
    val success: Boolean,
    val student_identified: String,
    val roll_no: String,
    val subject: String,
    val rubric_breakdown: List<Map<String, Any>>,
    val total_score: Int,
    val percentage: Double,
    val grade: String,
    val ready_for_teacher_approval: Boolean
)

data class OwnershipTransitionRequest(
    val school_id: String,
    val current_principal_mobile: String,
    val new_principal_name: String,
    val new_principal_mobile: String,
    val verification_otp: String
)

data class OwnershipTransitionResponse(
    val success: Boolean,
    val message: String,
    val tenant_id: String,
    val new_principal_mobile: String
)

interface GmptpApiService {
    @GET("api/v1/health")
    suspend fun checkHealth(): HealthCheckResponse

    @POST("api/v1/schools/register")
    suspend fun registerSchool(@Body request: PrincipalRegistrationRequest): PrincipalRegistrationResponse

    @POST("api/v1/ai/suvichar")
    suspend fun generateSuvichar(@Body request: AiSuvicharRequest): AiSuvicharResponse

    @POST("api/v1/ai/grade-exam")
    suspend fun gradeExamSheet(@Body request: ExamGradeOcrRequest): ExamGradeOcrResponse

    @POST("api/v1/schools/transfer-ownership")
    suspend fun transferSchoolOwnership(@Body request: OwnershipTransitionRequest): OwnershipTransitionResponse
}

object GmptpApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val retrofitService: GmptpApiService by lazy {
        retrofit.create(GmptpApiService::class.java)
    }
}
