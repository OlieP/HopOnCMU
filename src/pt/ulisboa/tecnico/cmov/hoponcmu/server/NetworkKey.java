package pt.ulisboa.tecnico.cmov.hoponcmu.server;


public enum NetworkKey {
	 //Type keys
    REQUEST_TYPE,
    REPLY_TYPE,

    //Request Keys
    USERNAME,
    PASSWORD,
    COUNTRY,

	
	//Reply Keys
    USER_INFO,
	MONUMENT_LIST,	
	QUIZ_LIST,
	QUESTION_LIST,
	
	//DATA
	SESSION_ID,
	TOUR_LIST,
	TOUR_NAME, 
	MONUMENT_NAME, 
	QUIZ_INFO,
	USER_ANSWERS,
	MONUMENT_SCORE,
	TOUR_SCORE;
	
    public String toString() {
        return Integer.toString(this.ordinal());
    }

}
