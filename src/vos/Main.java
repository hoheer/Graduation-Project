package vos;

import java.io.*;
import java.net.*;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;

//īī�� REST API ���̵���� ����
public class Main {
	  static boolean expired;  //������ ��ū�� ���� ���� ���� üũ
	  public static String get_auth_header(String grant_type, String client_id, String client_secret ) {
		
		  String access_token =  null;
		  //String refresh_token =  "";
	      String token_type = null;
	      Gson gson = new Gson();	      
	      File f = new File("oauth.json");
	      	 	        	       
	        if (f.exists()) {	//������ �߱޹��� ��ū�� �����ϴ� ���       	
	            System.out.println("���� ��ū ��ȿ�� �˻����Դϴ�...");
	            
	            try {
	            	token auth = new token();
	            	//�Ľ��� tokenŬ���� ��ü ����
	                JsonReader jsonReader = new JsonReader(new FileReader("oauth.json"));
	                
	                JsonObject jsonobject = new JsonObject();
	                JsonParser jsonParser = new JsonParser();
	                JsonElement element = jsonParser.parse(new FileReader("oauth.json"));
	                jsonobject = element.getAsJsonObject();
	                	                	          	             	    	             
	                auth = gson.fromJson(jsonReader, token.class);  //�Ľ� �޾ƿ�
	               
	                
	                long now = System.currentTimeMillis(); //��ū ��ȿ�� �˻翡 �ʿ��� ���� �ð� �� ����	               
	                if( now > auth.getExpires()) {
	                	 expired = true;//��ū �� �����ϰ� ���� �ð��� �� 
	                }
	                else expired = false;
	                
	                
	                if(expired==false) { //��ū�� ������� ���� ���
	                	access_token = auth.getAccess_token(); //���� access_token���� �״�� ���
	                	token_type = auth.getToken_type();
	                	System.out.println("���� ��ū�� ��ȿ�մϴ�");
	                }	
	                
	            } catch (Exception e) {
	                e.getMessage();
	            }	            	            	            	           
	        	}
	        	     	        
	        boolean request_new = false;
            if(access_token==null&&token_type==null) { //���� ��ū���� ���������� access_token ���� token_type���� �������� ������ ��� ���� ��ū�� �߱޹޴´�
                request_new = true;
            }

            if(request_new) {
            	//expired = true;
                System.out.println("���� ��ū�� ����Ǿ� ���� �߱޹޽��ϴ�....");                                             
                String reqURL = "https://api.avatarsdk.com/o/token/";  
                FileWriter writer = null;
                Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                token rsp = new token(); //�Ľ��� ���� ��ū ��ü ����
                
                try {
                	                	                	                	
                	URL url = new URL(reqURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");  //����Ÿ�� post
                    conn.setDoOutput(true);
                    
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                    StringBuilder sb = new StringBuilder();
                    
                    sb.append("grant_type="+grant_type);
                    sb.append("&client_id="+client_id);                   
                    sb.append("&client_secret="+client_secret);  
                    //������ �Ķ���� �� ����
                   
                    
                    bw.write(sb.toString());
                    bw.flush(); //����
                    
                    
                    int responseCode = conn.getResponseCode();
                    System.out.println("responseCode : " + responseCode); 
                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    String result = "";
                    
                    while ((line = br.readLine()) != null) {
                        result += line; //result�� �޾ƿ� ���� �ϳ��� string���� ����
                    }                  
                                     
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result);
                    
                    String json = gson2.toJson(element);
                                       
                    rsp= new Gson().fromJson(json, token.class);
                     
                    access_token = rsp.getAccess_token();
                    token_type = rsp.getToken_type();
                    
                    long now = System.currentTimeMillis();                                                                             
                    rsp.setExpires(now+(rsp.getExpires_in()-60)*1000);//��ū ���Ῡ�� üũ�� �ʿ��� ����ð� ����                   
                                                            
                    String create_as_file =  gson2.toJson(rsp); 
                    
                    try {
                    	//File file = new File("oauth.json");                    	
                        writer = new FileWriter("oauth.json");
                        writer.write(create_as_file);
                    } catch (IOException e) {
                        
                        e.printStackTrace();
                    }finally {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }                                
                    br.close();
                    bw.close();                	                	                	                	                          	            
        	        }                	                	                                                                       
                catch (IOException e) {                    
                    e.printStackTrace();                    
                }                              
	  }
           String headers = "{\"Authorization\":" + "\""+token_type+" "+access_token+"\""+"}" ; 
           //��ȯ�Ǵ� ������� json������� �� ������ �����־�� ��
           return headers;             
	  }
	  
public static String get_player_uid_header (String header) {
	String player_uid = null;
	

	File f = new File("player.json");
	Gson gson = new Gson();  
    if (f.exists()) { //���� player.json ������ �����ϴ� ���
    	System.out.println("���� ����� PlayerUID�� �ҷ��ɴϴ�");
   	
      try {    	
    	player player = new player();
    	   	
        JsonReader jsonReader = new JsonReader(new FileReader("player.json"));            
        JsonParser jsonParser = new JsonParser();
        //JsonElement element = jsonParser.parse(new FileReader("player.json"));      
              	                	          	             	    	             
        player = gson.fromJson(jsonReader, player.class);        
        player_uid = player.getCode(); 
        //���� ������ �����ϴ� ���  player_uid ���� ������
    	} 
    	catch (Exception e) {e.getMessage();}                        	    	
    }
    
    if (player_uid == null||expired==true) { //player_uid ���� �������� �ʴ� ��� ���� �߱� �޴´�
    	
    	
    	System.out.println("���ο� player�� �����մϴ�...");    	    	
    	//String player_form = "test_py";
    	
        String reqURL = "https://api.avatarsdk.com/players/";  
        FileWriter writer = null;
        Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
        player rsp = new player();
                       
        try {

        	header param = gson2.fromJson(header,header.class);
        	//������� authorization���� �̾ƿ��� ���� ��� ��ü�� �����ϰ� �Ľ��Ѵ�        	
        	URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", param.getAuthorization());
            //uid �߱޿� �ʿ��� �Ķ���� �� ����
            
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
            //result�� ����� ��� ����
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String json = gson2.toJson(element);
            
            rsp= new Gson().fromJson(json, player.class);  
            //player Ŭ������ �޾ƿ� json���� �Ľ�
            player_uid = rsp.getCode(); //uid�� ����
             
           System.out.println("PlayerUID�� ���Ϸ� �����մϴ�");
           String written_to_file =  gson2.toJson(rsp);
           try {
           	//File file = new File("player.json");           	
            writer = new FileWriter("player.json");
            writer.write(written_to_file);
            //player.json ���� �ۼ�
           } catch (IOException e) {               
               e.printStackTrace();
           }finally {
               try {
                   writer.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
	        }                	                	                                                                       
       catch (IOException e) {                    
           e.printStackTrace();                    
       }                                                                                                  	
        }    
         return "{\"X-PlayerUID\":" + "\""+player_uid+"\"}" ; 	
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AUTH_FORM AUTHFORM = new AUTH_FORM();	
		String header = get_auth_header(AUTHFORM.getGrant_type(),AUTHFORM.getClient_id(),AUTHFORM.getClient_secret());
		System.out.println(header);
		
		String player = get_player_uid_header(header);		
		System.out.println(player);
		
						   		 		  		   		 		   		   
	}

}
