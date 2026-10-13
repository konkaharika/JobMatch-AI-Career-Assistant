package com.jobportal.service;
import org.springframework.stereotype.Service; 
import java.util.*;
@Service public class EmbeddingService {
 public double[] embed(String text){double[] v=new double[64];if(text==null)return v;for(String token:text.toLowerCase().split("[^a-z0-9+#.]+")){if(token.isBlank())continue;int h=Math.abs(token.hashCode());for(int i=0;i<4;i++)v[(h+i*17)%v.length]+=1.0/(i+1);}double n=0;for(double x:v)n+=x*x;n=Math.sqrt(n);if(n>0)for(int i=0;i<v.length;i++)v[i]/=n;return v;}
 public double similarity(double[] a,double[] b){double s=0;for(int i=0;i<Math.min(a.length,b.length);i++)s+=a[i]*b[i];return Math.max(0,Math.min(1,s));}
 public String serialize(double[] v){return Arrays.toString(v);} public double[] deserialize(String s){if(s==null||s.isBlank())return new double[64];String x=s.replace("[","").replace("]","");String[] p=x.split(",");double[] v=new double[p.length];for(int i=0;i<p.length;i++)v[i]=Double.parseDouble(p[i].trim());return v;}
}
