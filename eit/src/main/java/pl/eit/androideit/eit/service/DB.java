package pl.eit.androideit.eit.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.eit.androideit.eit.service.model.Channel;
import pl.eit.androideit.eit.service.model.Message;

public class DB extends SQLiteOpenHelper {

	public SQLiteDatabase sqlDb;

	/*************** DEFINICJA BAZY DANYCH i KOLUMN TABEL **********************/

	private static final int DATABASE_VERSION = 8;
	private static final String DATABASE_NAME = "shoutboxDB.db";

	// ! TABELA WIADOMOSCI.
	private final String TABLE_MESSAGES = "Messages";
	private final String MESSAGES_ROW_ID = "_id";
	private final String MESSAGES_MESSAGE = "message";
	private final String MESSAGES_CHANNEL_TIMESTAMP = "channel_timestamp";
	private final String MESSAGES_USER_NAME = "user_name";
	private final String MESSAGES_MESSAGE_TIMESTAMP = "message_timestamp";

	// ! TABELA Kanałów.
	private static final String TABLE_CHANNELS = "Channels";
	private final String CHANNEL_ROW_ID = "_id";
	private final String CHANNEL_NAME = "channel_name";
    private final String CHANNEL_TIMESTAMP = "channel_timestamp";
    private final String CHANNEL_IS_SUB = "is_sub";   // 0 - nie jest, 1 - jest subowany
    private final String CHANNEL_LAST_SYNC = "last_sync";

	/**********************************************************/

	// ! KONSTRUKTOR
	public DB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/************* TWORZENIE TABEL ***************************/
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
				+ MESSAGES_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MESSAGES_MESSAGE + " TEXT NOT NULL," + MESSAGES_MESSAGE_TIMESTAMP
				+ " INTEGER UNIQUE ON CONFLICT IGNORE," + MESSAGES_USER_NAME + " TEXT,"
				+ MESSAGES_CHANNEL_TIMESTAMP + " INTEGER"  + ")";

        String CREATE_CHANNELS = "CREATE TABLE " + TABLE_CHANNELS + "("
                + CHANNEL_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CHANNEL_NAME + " TEXT NOT NULL, " + CHANNEL_TIMESTAMP +
                " INTEGER, " + CHANNEL_LAST_SYNC + " INTEGER DEFAULT 0," +
                CHANNEL_IS_SUB + " INTEGER DEFAULT 0" + ")";

		db.execSQL(CREATE_CHANNELS);
		db.execSQL(CREATE_MESSAGES);
		

		// Domyślnie do bazy wstawiane są kanały dla każdego roku.
		ContentValues values = new ContentValues();
		String[] years = { "I", "II", "III", "IV", "V" };
		for (int i=1; i<= years.length; i++) {
			values.put(CHANNEL_NAME, "Rok " + years[i-1]);
            values.put(CHANNEL_TIMESTAMP, i);
			db.insert(TABLE_CHANNELS, null, values);
		}

	}

/*	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}*/

	// ! UPDATE TABEL
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		onCreate(db);
	}

	// ! OTWIERANIE BAZY DANYCH
	public void openDb() {
		sqlDb = this.getWritableDatabase();
	}

	/************ MESSAGE ********************/
	/** Wstawia wiadomosc do bazy */
	public void insertMessage(Message message) {
		openDb();

		ContentValues values = new ContentValues();
		values.put(MESSAGES_MESSAGE, message.message);
		values.put(MESSAGES_MESSAGE_TIMESTAMP, message.messageTimestamp);
		values.put(MESSAGES_USER_NAME, message.userName);
		values.put(MESSAGES_CHANNEL_TIMESTAMP, message.channelTimestamp);

		sqlDb.insert(TABLE_MESSAGES, null, values);
		sqlDb.close();
	}

	/** Pobiera wiadomości dla danego kanału **/
	public ArrayList<Message> getMessagesForChannel(long channelTimestamp) {
		openDb();
		Cursor cursor = sqlDb.query(TABLE_MESSAGES, null, MESSAGES_CHANNEL_TIMESTAMP
				+ "=?", new String[] { String.valueOf(channelTimestamp) }, null, null,
				MESSAGES_MESSAGE_TIMESTAMP + " ASC");

		ArrayList<Message> result = new ArrayList<Message>();
		Message message;
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				message = new Message(cursor.getString(cursor
						.getColumnIndex(MESSAGES_MESSAGE)),
						cursor.getLong(cursor
                                .getColumnIndex(MESSAGES_CHANNEL_TIMESTAMP)),
						cursor.getLong(cursor
								.getColumnIndex(MESSAGES_MESSAGE_TIMESTAMP)),
						cursor.getString(cursor
								.getColumnIndex(MESSAGES_USER_NAME)));

				result.add(message);
			}
		}

		cursor.close();
		sqlDb.close();

		return result;
	}

    public Message getLastMessage(){
        openDb();
        Message message = null;

        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                MESSAGES_CHANNEL_TIMESTAMP + " IN( " +
                "SELECT " + CHANNEL_TIMESTAMP + " FROM " + TABLE_CHANNELS + " WHERE " +
                CHANNEL_IS_SUB + " =?) " + " ORDER BY " +
                MESSAGES_MESSAGE_TIMESTAMP + " DESC";
        Cursor cursor = sqlDb.rawQuery(query, new String[]{String.valueOf(1)});

        if(cursor.moveToFirst()){
                message = new Message(cursor.getString(cursor.getColumnIndex(MESSAGES_MESSAGE)),
                        cursor.getLong(cursor.getColumnIndex(MESSAGES_CHANNEL_TIMESTAMP)),
                        cursor.getLong(cursor.getColumnIndex(MESSAGES_MESSAGE_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(MESSAGES_USER_NAME)));
        }

        cursor.close();
        sqlDb.close();

        return message;
    }


    /** Zapisywanie wiadomości z serwera oraz aktualizacja daty ostatniego pobierania dla kanału.
     * @param data JSON z wiadomościami pobranymi z serwera
    **/
    public ArrayList<Message> saveMessagesFromServer(JSONArray data){
        openDb();

        JSONObject message;
        ArrayList<Message> newMessages = new ArrayList<Message>();
        Message newSingleMessage;
        ContentValues values = new ContentValues();
        String messageText, userName;
        long timestamp = 0, channelTimestamp = 0;

        for(int i=0; i<data.length(); i++){
            try {
                // Obiekt JSON wiadomości
                message = data.getJSONObject(i);
                // Poszczególne pola wiadomości
                messageText = message.getString("messageText");
                userName = message.getString("userName");
                timestamp = message.getLong("timestamp");
                channelTimestamp = message.getLong("channelTimestamp");

                // Wstawianie wiadomości do bazy
                values.clear();
                values.put(MESSAGES_MESSAGE, messageText);
                values.put(MESSAGES_MESSAGE_TIMESTAMP, timestamp);
                values.put(MESSAGES_CHANNEL_TIMESTAMP, channelTimestamp);
                values.put(MESSAGES_USER_NAME, userName);
                sqlDb.insert(TABLE_MESSAGES, null, values);

                // Dodaje wiadomość do listy, która będzie zwracana dla listy wiadomości kanału
                //newSingleMessage = new Message(messageText, channelTimestamp,timestamp,userName);
               // newMessages.add(newSingleMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Uaktualnianie czasu ostatniej synchronizacji danych dla kanału
        // w tym punkcie timestamp to timestamp najstarszej pobranej wiadomości,
        // więc może być czasem ostatniej synchronizacji.
        long lastUpdate = timestamp + 1;
        Log.d("lastUpdate", "s:" + lastUpdate);
        values.clear();
        values.put(CHANNEL_LAST_SYNC, lastUpdate);
        sqlDb.update(TABLE_CHANNELS, values, CHANNEL_TIMESTAMP + "=?",
                new String[]{String.valueOf(channelTimestamp)});

        sqlDb.close();
        return newMessages;
    }

	/************** CHANNELS ********************/

	/** Zwraca listę wszystkich kanałów **/
    public ArrayList<Channel> getChannels(){
        openDb();

        Channel channel;
        ArrayList<Channel> result = new ArrayList<Channel>();

        Cursor cursor = sqlDb.query(TABLE_CHANNELS, null, null, null, null, null, CHANNEL_TIMESTAMP + " ASC");
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                channel = new Channel(cursor.getLong(cursor
                        .getColumnIndex(CHANNEL_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(CHANNEL_NAME)),
                        cursor.getInt(cursor.getColumnIndex(CHANNEL_IS_SUB)));
                result.add(channel);
            }
        }

        return result;
    }


    /** Zwraca czas ostatniego pobrania wiadomości dla kanału **/
    public long getLastChannelSync(long channelTimestamp){
        openDb();

        Cursor cursor = sqlDb.query(TABLE_CHANNELS, new String[]{CHANNEL_LAST_SYNC},
                CHANNEL_TIMESTAMP+ "=?", new String[]{String.valueOf(channelTimestamp)}, null, null, null);
        long lastSync = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lastSync =  cursor.getLong(cursor.getColumnIndex(CHANNEL_LAST_SYNC));
        }
        sqlDb.close();

        return lastSync;
    }

    /** Zwraca jsona z subowanymi kanałami */
    public JSONObject getSubs(){
        openDb();

        JSONObject json = new JSONObject();
        Cursor cursor = sqlDb.query(TABLE_CHANNELS, new String[]{CHANNEL_TIMESTAMP},
                CHANNEL_IS_SUB + "=?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                try {
                    json.put("ch_timestamp",
                            cursor.getInt(cursor.
                                    getColumnIndex(CHANNEL_TIMESTAMP)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return json;
    }
/*	public ArrayList<Chanel> getChannels(String[] subscriptions) {
		ArrayList<Chanel> result = new ArrayList<Chanel>();
		Chanel channel;
		openDb();

		*//*
		 * Cursor cursor = sqlDb.query(TABLE_CHANNELS, null, CHANNEL_ROW_ID +
		 * " IN (??????)", subscriptions, null, null, CHANNEL_ROW_ID + " ASC");
		 *//*
		// Oblicza liczbę znaków zapytania potrzebnych do klauzuli IN
		StringBuilder builder = new StringBuilder();
		builder.append("?");
		for (int i = 1; i < subscriptions.length; i++) {
			builder.append(",?");
		}

		Cursor cursor = sqlDb.rawQuery("SELECT * FROM " + TABLE_CHANNELS
				+ " WHERE " + CHANNEL_TIMESTAMP + " IN (" + builder.toString()
				+ ")", subscriptions);
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				channel = new Chanel(cursor.getLong(cursor
                        .getColumnIndex(CHANNEL_TIMESTAMP)),
						cursor.getString(cursor.getColumnIndex(CHANNEL_NAME)),
                        cursor.getInt(cursor.getColumnIndex(CHANNEL_IS_SUB)));
				result.add(channel);
			}
		}

		cursor.close();
		sqlDb.close();

		return result;
	}*/

    /** Oznacza kanał jako subowany albo niesubowany
     *  w zaleznosci od parametru sub
     *  @param sub Obecny stan suba kanału. 1 - subowany 0 - niesubowany **/
    public void toggleChannelSub(long channelTimestamp, int sub){
        openDb();

        ContentValues values = new ContentValues();
        if(sub == 0){
            values.put(CHANNEL_IS_SUB, 1);
        }
        else if(sub == 1){
            values.put(CHANNEL_IS_SUB, 0);
        }

        sqlDb.update(TABLE_CHANNELS, values, CHANNEL_TIMESTAMP + "=?", new String[]{String.valueOf(channelTimestamp)} );
        sqlDb.close();
    }

}
