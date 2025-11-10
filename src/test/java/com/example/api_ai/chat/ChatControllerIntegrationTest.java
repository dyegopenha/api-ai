package com.example.api_ai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ChatController.class)
class ChatControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private ChatService chatService;

   @Autowired
   private ObjectMapper objectMapper;

   @Test
   void testChatEndpointIntegration() throws Exception {
      // Given
      String userMessage = "Hello, Spring AI!";
      String aiResponse = "Hello! I'm Spring AI, how can I help you today?";
      ChatRequest request = new ChatRequest(userMessage);

      when(chatService.chat(userMessage)).thenReturn(aiResponse);

      // When & Then
      mockMvc.perform(post("/api/chat")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
             .andExpect(jsonPath("$.message").value(aiResponse));

      verify(chatService, times(1)).chat(userMessage);
   }

   @Test
   void testChatWithDifferentMessageTypesIntegration() throws Exception {
      // Given
      String userMessage = "Can you help me with Java Spring Framework?";
      String aiResponse = "Absolutely! I'd be happy to help you with Spring Framework questions.";
      ChatRequest request = new ChatRequest(userMessage);

      when(chatService.chat(userMessage)).thenReturn(aiResponse);

      // When & Then
      mockMvc.perform(post("/api/chat")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
             .andDo(print())
             .andExpect(status().isOk())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
             .andExpect(jsonPath("$.message").value(aiResponse));

      verify(chatService, times(1)).chat(userMessage);
   }

   @Test
   void testChatRequestValidationIntegration() throws Exception {
      // Given - malformed JSON
      String malformedJson = "{ \"message\": }";

      // When & Then
      mockMvc.perform(post("/api/chat")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(malformedJson))
             .andDo(print())
             .andExpect(status().isBadRequest());

      verify(chatService, never()).chat(anyString());
   }

   @Test
   void testChatEndpointMapping() throws Exception {
      // Given
      String userMessage = "Test endpoint mapping";
      String aiResponse = "Endpoint is working correctly";
      ChatRequest request = new ChatRequest(userMessage);

      when(chatService.chat(userMessage)).thenReturn(aiResponse);

      // When & Then - Test that the endpoint is correctly mapped
      mockMvc.perform(post("/api/chat")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
             .andExpect(status().isOk())
             .andExpect(handler().handlerType(ChatController.class))
             .andExpect(handler().methodName("chat"));

      verify(chatService, times(1)).chat(userMessage);
   }
}
