package com.gardenagent.data.remote.claude.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeResponse(
    val id: String,
    val content: List<ClaudeContentBlock>,
    @SerialName("stop_reason") val stopReason: String,
    val usage: ClaudeUsage,
)

@Serializable
data class ClaudeContentBlock(
    val type: String,
    val text: String? = null,
)

@Serializable
data class ClaudeUsage(
    @SerialName("input_tokens") val inputTokens: Int,
    @SerialName("output_tokens") val outputTokens: Int,
)
