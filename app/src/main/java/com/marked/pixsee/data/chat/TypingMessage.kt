package com.marked.pixsee.data.chat

/**
 * Created by Tudor on 25-Nov-16.
 */

data class TypingMessage(val from: String?,
                         val fromToken: String?,
                         val to: String?,
                         val toToken: String?,
                         val typing: Boolean?)