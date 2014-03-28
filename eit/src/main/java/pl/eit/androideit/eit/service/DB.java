package pl.eit.androideit.eit.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import pl.eit.androideit.eit.service.model.Chanel;
import pl.eit.androideit.eit.service.model.Message;

public class DB extends SQLiteOpenHelper {

	public SQLiteDatabase sqlDb;

	/*************** DEFINICJA BAZY DANYCH i KOLUMN TABEL **********************/

	private static final int DATABASE_VERSION = 3;
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
				+ " INTEGER," + MESSAGES_USER_NAME + " TEXT,"
				+ MESSAGES_CHANNEL_TIMESTAMP + " INTEGER" /** REFERENCES " + CHANNEL_ROW_ID
				+ " ON DELETE CASCADE" **/ + ")";

		String CREATE_CHANNELS = "CREATE TABLE " + TABLE_CHANNELS + "("
				+ CHANNEL_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CHANNEL_NAME + " TEXT NOT NULL, " + CHANNEL_TIMESTAMP + " INTEGER" + ")";

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

	/************** CHANNELS ********************/
	/** Zwraca listę subskrybowanych kanałów **/
	public ArrayList<Chanel> getChannels(String[] subscriptions) {
		ArrayList<Chanel> result = new ArrayList<Chanel>();
		Chanel channel;
		openDb();

		/*
		 * Cursor cursor = sqlDb.query(TABLE_CHANNELS, null, CHANNEL_ROW_ID +
		 * " IN (??????)", subscriptions, null, null, CHANNEL_ROW_ID + " ASC");
		 */
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
						cursor.getString(cursor.getColumnIndex(CHANNEL_NAME)));
				result.add(channel);
			}
		}

		cursor.close();
		sqlDb.close();

		return result;
	}

}
