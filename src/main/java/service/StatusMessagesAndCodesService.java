package service;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import server_utilities.CSVReader;
import database_entities.exceptions.LoadDBWithCSVFailed;
import database_entities.exceptions.StatusMessageDoesNotExist;
import database_entities.utilities.StatusMessagesAndCodes;
import database_entities.utilities.StatusMessagesAndCodesRepository;


@Service
public class StatusMessagesAndCodesService {

	/* Convenient references to the codes located in the servererrors.csv file */
	public static int REGISTRATION_OKAY 						= 100;
	public static int REGISTRATION_DUPLICATE_EMAIL 				= 101;
	public static int REGISTRATION_DUPLICATE_USERNAME 			= 102;
	public static int REGISTRATION_INVALID_FORM 				= 103;
	public static int LOGIN_OKAY 								= 110;
	public static int LOGIN_USER_PASS_BAD 						= 111;
	public static int LOGIN_EMAIL_PASS_BAD 						= 112;
	public static int LOGOUT_OKAY 								= 120;
	public static int LOGOUT_FAILED_SESSION 					= 121;
	public static int LOGOUT_FAILED_OTHER 						= 122;
	public static int UPDATE_PREF_SUCCESS 						= 130;
	public static int UPDATE_PREF_FAIL_DATE 					= 131;
	public static int UPDATE_PREF_FAIL_FORM 					= 132;
	public static int UPDATE_ADDR_SUCCESS 						= 140;
	public static int UPDATE_ADDR_FAIL_FORM 					= 141;
	public static int UPDATE_ADDR_FAIL_INVALID 					= 142;
	public static int UPDATE_CURRENT_LOC_SUCCESS 				= 150;
	public static int UPDATE_CURRENT_LOC_FAILED 				= 151;
	public static int RETRIEVE_FRIENDS_SUCCESS					= 160;
	public static int RETRIEVE_FRIENDS_FAIL_NONE 				= 161;
	public static int SEND_FRIEND_REQ_SUCCESS 					= 170;
	public static int SEND_FRIEND_REQ_FAIL_NO_USER 				= 171;
	public static int SEND_FRIEND_REQ_FAIL_FRIEND_EXISTS 		= 172;
	public static int SEND_FRIEND_REQ_FAIL_BLOCKED 				= 173;
	public static int SEND_FRIEND_REQ_FAIL_PENDING 				= 174;
	public static int RESPONSE_FRIEND_REQ_SUCCESS 				= 176;
	public static int RESPONSE_FRIEND_REQ_FAIL_NONE 			= 177;
	public static int RESPONSE_FRIEND_REQ_FAIL_DUPLICATE 		= 178;
	public static int RESPONSE_FRIEND_REQ_FAIL_INVALID 			= 179;
	public static int TRADE_ITEM_CREATION_SUCCESS 				= 200;
	public static int TRADE_ITEM_CREATION_FAILED_FORM 			= 201;
	public static int TRADE_ITEM_CREATION_NULL_VALUES 			= 202;
	public static int TRADE_ITEM_CREATION_FAILED_MAX_LIMIT 		= 203;
	public static int IMAGE_SAVE_SUCCESS 						= 210;
	public static int IMAGE_SAVE_FAIL_NO_ITEM 					= 211;
	public static int IMAGE_SAVE_FAIL_WRONG_OWNER 				= 212;
	public static int IMAGE_SAVE_FAIL_MAX_IMAGES 				= 213;
	public static int IMAGE_RECV_SUCCESS 						= 220;
	public static int IMAGE_RECV_FAILED_NON_EXISTENT 			= 221;
	public static int IMAGE_RECV_FAILED_NO_IMAGES 				= 222;
	public static int IMAGE_DELETE_SUCCESS 						= 230;
	public static int IMAGE_DELETE_FAILED_NO_ITEM 				= 231;
	public static int IMAGE_DELETE_FAILED_NO_IMAGE 				= 232;
	public static int IMAGE_DELETE_FAILED_INVALID_USER 			= 233;
	public static int RECV_ITEMS_SUCCESS 						= 240;
	public static int RECV_ITEMS_FAILED_NO_ITEMS 				= 241;
	public static int UPDATE_ITEM_SUCCESS 						= 250;
	public static int UPDATE_ITEM_FAILED_NO_ITEM 				= 251;
	public static int UPDATE_ITEM_FAILED_WRONG_OWNER 			= 252;
	public static int UPDATE_ITEM_FAILED_INVALID_FORM 			= 253;
	public static int DELETE_ITEM_SUCCESS 						= 260;
	public static int DELETE_ITEM_FAILED_NO_ITEM 				= 261;
	public static int DELETE_ITEM_FAILED_WRONG_OWNER 			= 262;
	public static int UPDATE_EMAIL_SUCCESS 						= 270;
	public static int UPDATE_EMAIL_FAILED_INVALID_EMAIL 		= 271;
	public static int UPDATE_EMAIL_FAILED_DUPLICATE_EMAIL 		= 272;
	public static int TRADE_META_RETRV_SUCCESS 					= 280;
	public static int TRADE_META_RETRV_FAILED_NO_USER			= 281;
	public static int TRADE_META_RETRV_FAILED_PERMISSIONS 		= 282;
	public static int COUNTER_TRADE_REQ_SUCCESS					= 289;
	public static int SEND_TRADE_REQ_SUCCESS 					= 290;
	public static int SEND_TRADE_REQ_FAIL_NO_USER 				= 291;
	public static int SEND_TRADE_REQ_FAIL_INVALID_ITEMS 		= 292;
	public static int SEND_TRADE_REQ_FAIL_TRADE_METHOD 			= 293;
	public static int SEND_TRADE_REQ_FAIL_NO_ITEMS 				= 294;
	public static int SEND_TRADE_REQ_FAIL_SAME_REQUEST 			= 295;
	public static int RESP_TRADE_REQ_SUCCESS					= 296;
	public static int RESP_TRADE_REQ_FAILED_WRONG_OWNER			= 297;
	public static int RESP_TRADE_REQ_FAILED_INVALID_STATUS		= 298;
	public static int TRADE_REQ_NON_EXISTENT					= 299;
	public static int UPDATE_GEOLOCATION_SUCCESS 				= 300;
	public static int UPDATE_GEOLOCATION_FAILED 				= 301;
	public static int TRADE_REQ_NOT_PENDING						= 302;
	public static int CANCEL_TRADE_REQ_SUCCESS					= 305;
	public static int CANCEL_TRADE_REQ_FAILED_INVALID_USER		= 306;
	public static int CANCEL_TRADE_REQ_FAILED_INVALID_STATUS	= 307;
	public static int RESP_TRADE_REQ_FAILED_USER_NOT_TARGET		= 308;
	public static int CLEAR_TRADE_REQ_SUCCESS					= 310;
	public static int CLEAR_TRADE_REQ_FAILED_INVALID_USER		= 311;
	public static int CLEAR_TRADE_REQ_FAILED_INVALID_STATUS		= 312;
	public static int MARK_TRADE_REQ_READ_SUCCESS				= 313;
	public static int MARK_TRADE_REQ_FAILED_INVALID_USER		= 314;
	public static int MARK_TRADE_REQ_FAILED_INVALID_STATUS 		= 315;
	public static int MARK_TRADE_REQ_FAILED_ALREADY_MARKED		= 316;
	public static int SEARCH_SUCCESSFUL 						= 400;
	public static int SEARCH_FAILED 							= 401;
	public static int SEARCH_EMPTY 								= 402;
	public static int RETRIEVE_USER_DATA_SUCCESS 				= 500;
	public static int RETRIEVE_USER_DATA_FAILED_NO_USER 		= 501;
	public static int RETRIEVE_USER_DATA_FAILED_BLOCKED 		= 502;
	public static int FACEBOOK_LOGIN_SUCCESS 					= 510;
	public static int FACEBOOK_LOGIN_INVALID 					= 511;
	public static int GET_REQUEST_SUCCESS 						= 800;
	public static int GET_REQUEST_FAILED 						= 801;
	public static int SESSION_SET_FAILED 						= 900;
	public static int SESSION_NON_EXISTENT 						= 901;
	public static int JSON_PARSE_ERROR 							= 902;
	public static int DATABASE_ERROR 							= 998;
	public static int UNKNOWN_SERVER_ERROR 						= 999;
	
	
	transient private static Logger log = LoggerFactory.getLogger("service-logger");
	
	private StatusMessagesAndCodesRepository repo;	
	
	@Autowired
	public StatusMessagesAndCodesService(StatusMessagesAndCodesRepository repo) {
		this.repo = repo;
		populateDBWithCSVErrors();
	}
	
	/**
	 * Populate the DB with all known error codes and messages
	 * This should on be done on Service start or if there is a need to update during runtime
	 * @throws LoadDBWithCSVFailed
	 * @return success
	 */
	public void populateDBWithCSVErrors() throws LoadDBWithCSVFailed {
		
		try {
			CSVReader csv = new CSVReader("servererrors");
			//Clear the collection first
			repo.deleteAll();
			//Then repopulate
			ArrayList<String[]> arr = csv.getCSVArray();
			
			for(String[] next: arr) {
				if(next.length != 0)
					repo.save(new StatusMessagesAndCodes(Integer.parseInt(next[0]), next[1]));
			}
		}
		catch(IOException ie) {
			log.error("Failed to load the server errors resource file: " + ie.getLocalizedMessage());
			repo.deleteAll();
			throw new LoadDBWithCSVFailed("IO Error");
		}
		catch(DuplicateKeyException dke) {
			log.error("There were keys with duplicate status codes: " + dke.getLocalizedMessage());
			repo.deleteAll();
			throw new LoadDBWithCSVFailed("Duplicate Key Error");
		}
	}
	
	/**
	 * Retrieve a ServerMessage by a code, (No data wil be populated)
	 * 
	 * @param code
	 * @return ServerMessage
	 * @throws StatusMessageDoesNotExist
	 */
	public ServerMessage getMessageForCode(int code) throws StatusMessageDoesNotExist {
		return getMessageWithData(code, null);
	}
	
	/**
	 * Retrieve a ServerMessage by a code, and populate it with the specific Data
	 * @param code
	 * @param data
	 * @return ServerMessage
	 * @throws StatusMessageDoesNotExist
	 */
	public ServerMessage getMessageWithData(int code, Object data) throws StatusMessageDoesNotExist {
		if(code < 100)
			throw new StatusMessageDoesNotExist("Error codes must be >= 100");
		
		StatusMessagesAndCodes retMessage = repo.findOne(code);
		if (retMessage == null)
			throw new StatusMessageDoesNotExist("Message Code does not exist");

		log.debug("Retrieved message from DB: " + retMessage.getMessage() + " with code: " + code);
		return new ServerMessage(code, data, retMessage.getMessage());		
	}
	
	
}
