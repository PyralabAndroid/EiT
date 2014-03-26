package pl.eit.androideit.eit.chanel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

	public SQLiteDatabase sqlDb;

	/*************** DEFINICJA BAZY DANYCH i KOLUMN TABEL **********************/

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "shoutboxDB.db";

	// ! TABELA WIADOMOSCI.
	private final String TABLE_MESSAGES = "Messages";
	private final String MESSAGES_ROW_ID = "_id";
	private final String MESSAGES_MESSAGE = "message";
	private final String MESSAGES_CHANNEL_ID = "channel_id";
	private final String MESSAGES_USER_NAME = "user_name";
	private final String MESSAGES_MESSAGE_DATE = "message_date";

	// ! TABELA Kanałów.
	private static final String TABLE_CHANNELS = "Channels";
	private final String CHANNEL_ROW_ID = "_id";
	private final String CHANNEL = "channel";

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
				+ MESSAGES_MESSAGE + " TEXT NOT NULL," + MESSAGES_MESSAGE_DATE
				+ " INTEGER," + MESSAGES_USER_NAME + " TEXT,"
				+ MESSAGES_CHANNEL_ID + " INTEGER" /** REFERENCES " + CHANNEL_ROW_ID
				+ " ON DELETE CASCADE" **/ + ")";

		String CREATE_CHANNELS = "CREATE TABLE " + TABLE_CHANNELS + "("
				+ CHANNEL_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CHANNEL + " TEXT NOT NULL" + ")";

		db.execSQL(CREATE_CHANNELS);
		db.execSQL(CREATE_MESSAGES);
		

		// Domyślnie do bazy wstawiane są kanały dla każdego roku.
		ContentValues values = new ContentValues();
		String[] years = { "I", "II", "III", "IV", "V" };
		for (String year : years) {
			values.put(CHANNEL, "Rok " + year);
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
	public void insertMessage(MessageObject message) {
		openDb();

		ContentValues values = new ContentValues();
		values.put(MESSAGES_MESSAGE, message.message);
		values.put(MESSAGES_MESSAGE_DATE, message.messageDate);
		values.put(MESSAGES_USER_NAME, message.userName);
		values.put(MESSAGES_CHANNEL_ID, message.channelId);

		sqlDb.insert(TABLE_MESSAGES, null, values);
		sqlDb.close();
	}

	/** Pobiera wiadomości dla danego kanału **/
	public ArrayList<MessageObject> getMessagesForChannel(int channel) {
		openDb();
		Cursor cursor = sqlDb.query(TABLE_MESSAGES, null, MESSAGES_CHANNEL_ID
				+ "=?", new String[] { String.valueOf(channel) }, null, null,
				MESSAGES_MESSAGE_DATE + " ASC");

		ArrayList<MessageObject> result = new ArrayList<MessageObject>();
		MessageObject message;
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				message = new MessageObject(cursor.getString(cursor
						.getColumnIndex(MESSAGES_MESSAGE)),
						cursor.getInt(cursor
								.getColumnIndex(MESSAGES_CHANNEL_ID)),
						cursor.getLong(cursor
								.getColumnIndex(MESSAGES_MESSAGE_DATE)),
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
	public ArrayList<ChannelObject> getChannels(String[] subscriptions) {
		ArrayList<ChannelObject> result = new ArrayList<ChannelObject>();
		ChannelObject channel;
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
				+ " WHERE " + CHANNEL_ROW_ID + " IN (" + builder.toString()
				+ ")", subscriptions);
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				channel = new ChannelObject(cursor.getInt(cursor
						.getColumnIndex(CHANNEL_ROW_ID)),
						cursor.getString(cursor.getColumnIndex(CHANNEL)));
				result.add(channel);
			}
		}

		cursor.close();
		sqlDb.close();

		return result;
	}

}
