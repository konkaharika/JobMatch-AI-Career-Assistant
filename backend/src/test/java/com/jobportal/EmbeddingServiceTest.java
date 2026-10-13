package com.jobportal;
import com.jobportal.service.EmbeddingService;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class EmbeddingServiceTest{@Test void similarTextHasPositiveSimilarity(){var e=new EmbeddingService();assertTrue(e.similarity(e.embed("Java Spring Boot MySQL"),e.embed("Java Spring Boot REST"))>0);}@Test void vectorIsNormalized(){var e=new EmbeddingService();double[] v=e.embed("React Vite Axios");double sum=0;for(double x:v)sum+=x*x;assertEquals(1,sum,1e-9);}}
