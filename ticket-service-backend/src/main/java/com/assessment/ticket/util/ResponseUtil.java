package com.assessment.ticket.util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.assessment.ticket.enums.ResponseConstants;

public class ResponseUtil {
	
	private final static Logger log = Logger.getLogger(ResponseUtil.class.getName());
	
	public Object buildAndReturnResponse(String message, Object data, String status) {
		HashMap<String,Object> returnObj = new HashMap<String,Object>();
		try {
			returnObj.put(ResponseConstants.STATUS.getName(), status);
			returnObj.put(ResponseConstants.MESSAGE.getName(), message);
			returnObj.put(ResponseConstants.DATA.getName(), data);
		}
		catch(Exception e) {
			log.log(Level.SEVERE,e.getMessage());
		}
		return returnObj;
	}

}
