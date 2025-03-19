//package com.grassland.tagging.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CsvBatchService {
//    private static final String API_URL = "http://localhost:8080/api/questions/batch"; // 질문 등록 API URL
//    private static final int BATCH_SIZE = 10; // 한 번에 보낼 데이터 개수
//
//     //이미 보낸 질문, 프로그램을 다시 시작했을 때 보냈던 질문을 기억해내는 코드를 넣기! 그래서 중복 줄이기.
//
//    // 이미 전송된 질문을 추적하기 위한 Set
//    private final Set<String> sentQuestions = new HashSet<>(); // Set to track sent questions
//
//    public void processCsvBatch(String filePath) throws IOException {
//        List<Map<String, String>> questions = readCsv(filePath); // CSV 파일 읽기 (context -> question 변경됨)
//        log.info("Total questions to send: {}", questions.size());
//        sendToApiInBatches(questions); // 배치 전송
//    }
//
//    private List<Map<String, String>> readCsv(String filePath) throws IOException {
//        List<Map<String, String>> questionList = new ArrayList<>();
//
//        try (FileReader reader = new FileReader(filePath, StandardCharsets.UTF_8)) {
//            Iterable<CSVRecord> records = CSVFormat.DEFAULT
//                    .withHeader()  // 헤더를 자동으로 처리
//                    .withSkipHeaderRecord()  // 첫 번째 줄을 건너뜀
//                    .parse(reader);
//
//            for (CSVRecord record : records) {
//                Map<String, String> questionMap = new HashMap<>();
//                for (String header : record.toMap().keySet()) {
//                    String value = record.get(header).trim();
//                    String key = header.equals("context") ? "question" : header;
//                    questionMap.put(key, value);
//                }
//                String question = questionMap.get("question");
//                if (question != null && !sentQuestions.contains(question)) {
//                    questionList.add(questionMap); // 새로운 질문만 추가
//                    sentQuestions.add(question); // 전송한 질문으로 등록
//                }
//            }
//        }
//
//        return questionList;
//    }
//
//
//    private void sendToApiInBatches(List<Map<String, String>> questions) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        int successCount = 0;
//
//        // 10개씩 끊어서 처리(Batch Size 변경 가능)
//        for (int i = 0; i < questions.size(); i += BATCH_SIZE) {
//            int batchEnd = Math.min(i + BATCH_SIZE, questions.size());
//            List<Map<String, String>> batch = questions.subList(i, batchEnd);
//
//            log.info("현재 처리 중: {} ~ {}", i, batchEnd);
//
//            try {
//                HttpEntity<List<Map<String, String>>> requestEntity = new HttpEntity<>(batch, headers);
//                ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);
//
//                log.info("응답 본문: {}", response.getBody());
//
//                if (response.getStatusCode().is2xxSuccessful()) {
//                    // API 응답에서 실제 추가된 개수를 추출
//                    int addedCount = extractAddedCount(response.getBody());
//                    successCount += addedCount;
//                    log.info("Batch 성공: {}개 추가됨", addedCount);
//                } else {
//                    log.error("Batch 실패: {}", response.getStatusCode());
//                    log.error("응답 본문: {}", response.getBody());
//                }
//            } catch (Exception e) {
//                log.error("API 요청 중 오류 발생:", e);             }
//        }
//        log.info("총 {}개 질문이 성공적으로 추가됨", successCount);
//    }
//    private static int extractAddedCount(String responseBody) {
//        try {
//            long count = responseBody.split("Question is created").length-1;
//            return (int) count;
//        } catch (NumberFormatException e) {
//            return 0; // 숫자로 변환 실패하면 0 반환
//        }
//    }
//}
