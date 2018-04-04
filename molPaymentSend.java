package io.gs.pc.ng.main;

import java.io.PrintWriter;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.util.Pair;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import io.gupshup.modal.GooleImageResponse;
import io.gupshup.utility.Constants;

/**
 * Servlet implementation class MolPayment
 */
@WebServlet("/molPaymentSend")
public class MolPaymentSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MolPaymentSend() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**Abhinay Gupta
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		final String applicationCode = "0xvon0HvIZlmyc2P0WY5QTmH5gncMqPu";
		final String version = "v1";
//		final int amount = 1000;//9900;
//		final String currencyCode = "usd";//"php";

		int amount = 0;
		String currencyCode = "usd";

		final String description = "NearGroup payment";
		final String Secret_Key = "xcaLKxZafwIJd1zmxtv5nqJZ5GK1gNmR";
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		if(br != null){
			json = br.readLine();
		}
		br.close();
		JSONObject jsonObject = new JSONObject(json);
		JSONObject mainobj = new JSONObject();
		String paymentType = jsonObject.get("paymentType").toString();
		System.out.println("got paymentType=" + paymentType);
		HttpSession session=request.getSession();
		session.setAttribute("sessionID", request.getSession().getId());
		session.setAttribute("channelId", jsonObject.get("chid").toString());
		session.setAttribute("channelAuth", jsonObject.get("chauth").toString());
		session.setAttribute("applicationCode", applicationCode);
		session.setAttribute("version", version);
		if(paymentType.equals("OTHER_OPTIONS")) {
			amount = Integer.parseInt(jsonObject.get("amount").toString());
			currencyCode = jsonObject.get("currencyCode").toString();

			System.out.println("amount and currenty= " + Integer.toString(amount) + currencyCode);

			session.setAttribute("amount", amount);
			session.setAttribute("currencyCode", currencyCode);
		}

		session.setAttribute("description", description);
		String channelId = ""+new Random().nextInt(999);
		String referenceId = "TRX"+channelId+System.currentTimeMillis();
		String customerId = jsonObject.get("chid").toString();

		session.setAttribute("referenceId", referenceId);
		String returnUrl = Constants.FACEBOOK.MyUrl55+"molPaymentSuccess?referenceId="+referenceId;
//		String returnUrl = "https://e6311a22.ngrok.io/NG/molPaymentSuccess?referenceId="+referenceId;
		session.setAttribute("returnUrl", returnUrl);

		session.setAttribute("customerId", customerId);

		String Signature = "";
		if(paymentType.equals("CARRIER_BILLING")) {
			System.out.println("CARRIER_BILLING");
			Signature  =   applicationCode  +    customerId  +  description
					+  referenceId  +  returnUrl  +  version  + Secret_Key;
			System.out.println("signature= " + Signature);
		} else if(paymentType.equals("OTHER_OPTIONS")) {
			System.out.println("OTHER_OPTIONS");
			Signature = amount  +  applicationCode  +  currencyCode  +  customerId  +  description
					+  referenceId  +  returnUrl  +  version  + Secret_Key;
			System.out.println("signature= " + Signature);
		}



		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(Signature.getBytes(),0,Signature.length());
			Signature = new BigInteger(1,m.digest()).toString(16);
			session.setAttribute("Signature", Signature);

			String id=DBHandler.getInstance().addData_advice("PaymentUserHistory", new String[] { "channelId", "referenceId", "signature", "currencyCode", "amount" }, Arrays.asList(customerId, referenceId, Signature, currencyCode, (amount/100)+""));
			session.setAttribute("id", id);

//			List<Pair> nameValuePair = new ArrayList<Pair>(9);
//            nameValuePair.add(new Pair("applicationCode", applicationCode));
//            nameValuePair.add(new Pair("referenceId", referenceId));
//            nameValuePair.add(new Pair("version", version));
//            nameValuePair.add(new Pair("amount", amount));
//            nameValuePair.add(new Pair("currencyCode", currencyCode));
//            nameValuePair.add(new Pair("description", description));
//            nameValuePair.add(new Pair("returnUrl", returnUrl));
//            nameValuePair.add(new Pair("customerId", customerId));
//            nameValuePair.add(new Pair("Signature", Signature));
//
//            URL url = new URL("https://sandbox-api.mol.com/payout/payments");
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setUseCaches(false);
//            httpURLConnection.setDoInput(true);
//
//          //send request
//            httpURLConnection.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//            wr.writeBytes(getQuery(nameValuePair));
//            wr.flush();
//            wr.close();
//
//            //Get Response
//            String line="";
//            InputStream is = httpURLConnection.getInputStream();
//            System.out.println(is);
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            System.out.println(rd);
//            StringBuffer response2 = new StringBuffer();
//            System.out.println(response2);
//            while ((line = rd.readLine()) != null) {
//                    response2.append(line);
//                    response2.append('\r');
//            }
//            rd.close();

			//config for carrier billing
//			amount = '';
//			currencyCode = '';

			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//			if(paymentType.equals("CARRIER_BILLING")) {
				System.out.println("CARRIER_BILLING");
				RequestBody body = RequestBody.create(mediaType,
						"applicationCode="+applicationCode
						+"&referenceId="+referenceId
						+"&version="+version
						+"&returnUrl="+returnUrl
						+"&description="+description
						+"&customerId="+customerId
						+"&Signature="+Signature);
//			} else
			if(paymentType.equals("OTHER_OPTIONS")) {
				System.out.println("OTHER_OPTIONS");
				body = RequestBody.create(mediaType,
						"applicationCode="+applicationCode
						+"&referenceId="+referenceId
						+"&version="+version
						+"&amount="+amount
						+"&currencyCode="+currencyCode
						+"&returnUrl="+returnUrl
						+"&description="+description
						+"&customerId="+customerId
						+"&Signature="+Signature);
			}

			System.out.println("final signature= "+ Signature);

			Request request2 = new Request.Builder()
			  .url("https://sandbox-api.mol.com/payout/payments")
			  .post(body)
			  .addHeader("content-type", "application/x-www-form-urlencoded")
			  .addHeader("cache-control", "no-cache")
			  .build();

			Response response2 = client.newCall(request2).execute();
			final int code = response2.code();
			boolean returnValue = false;
			if (code == 200) {
				String t = response2.body().string();
				System.out.println("WORK!" + t);
				/*GooleImageResponse result = GSON.fromJson(t, GooleImageResponse.class);
				if (result.getResponses().get(0).getSafeSearchAnnotation().getAdult().contains("UNLIKELY")) {
					returnValue = true;
				}*/
				jsonObject = new JSONObject(t);

				String url=jsonObject.get("paymentUrl").toString();
				String paymentId=jsonObject.get("paymentId").toString();

				session.setAttribute("paymentId", jsonObject.get("paymentId").toString());
				mainobj.put("url", url);
				mainobj.put("paymentId", paymentId);
			} else {
				String t = response2.body().string();
				System.out.println("not WORK!" + t);
				mainobj.put("Grant_Access", false);
			}
			DBHandlerSlave.getInstance().updateData("PaymentUserHistory", new String[] { "APIResponseCode1" }, Arrays.asList( ""+code ),"id = '"+id+"' AND referenceId = '"+referenceId+"'");
            System.out.println(response2);
    		response2.close();

			mainobj.put("Grant_Access", true);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mainobj.put("Grant_Access", false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mainobj.put("Grant_Access", false);
		}
		System.out.println(mainobj);
		response.getWriter().println(mainobj);
		response.getWriter().flush();
		response.getWriter().write("");
		response.getWriter().close();
		return;

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private String getQuery(List<Pair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey().toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }

}
