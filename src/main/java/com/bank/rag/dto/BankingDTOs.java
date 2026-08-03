package com.bank.rag.dto;

import lombok.Data;
import java.util.List;

public class BankingDTOs {
    @Data public static class ChatReq { private String query; }
    @Data public static class ChatResp { private String answer; private List<String> sources; }
}
