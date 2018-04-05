package com.lava.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lava.game.FloorIsLava;
import com.lava.game.states.MenuState;
import com.lava.game.states.PlayState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AndroidLauncher extends AndroidApplication implements PlayServices{


	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	private final static int requestCode = 1;
	private GoogleSignInClient mGoogleSignInClient = null;
	// Debug tag
	static final String TAG = "LavaHelper";
	// Holds the configuration of the current room.
	RoomConfig mRoomConfig;
	// Room ID where the currently active game is taking place; null if we're not playing.
	String mRoomId = null;
	// Are we playing in multiplayer mode?
	boolean mMultiplayer = false;
	// The participants in the currently active game
	ArrayList<Participant> mParticipants = null;
	// My participant ID in the currently active game
	String mMyId = null;
	private String mPlayerId;
	// Client used to interact with the real time multiplayer system.
	private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;
	// Score of other participants. We update this as we receive their scores
	// from the network.
	Map<String, Integer> mParticipantScore = new HashMap<>();

	// Participants who sent us their final score.
	Set<String> mFinishedParticipants = new HashSet<>();
	// The currently signed in account, used to check the account has changed outside of this
	// activity when resuming.
	GoogleSignInAccount mSignedInAccount = null;

	// are we already playing?
	boolean mPlaying = false;

	// at least 2 players required for our game
	final static int MIN_PLAYERS = 2;

	// Message buffer for sending messages
	byte[] mMsgBuf = new byte[2];

	PlayState playState;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGoogleSignInClient = GoogleSignIn.getClient(this,
													 GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

		signInSilently();

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


		initialize(new FloorIsLava(this), config);

		// Keep screen on
		graphics.getView().setKeepScreenOn(true);
	}

	MenuState mState = null;

	@Override
	public void startQuickGame(MenuState mState) {
		debugLog("Initiated quick game! ");
		this.mState = mState;
		// quick-start a game with 1 randomly selected opponent
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
				MAX_OPPONENTS, 0);


		mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
				.setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
				.setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
				.setAutoMatchCriteria(autoMatchCriteria)
				.build();
		mRealTimeMultiplayerClient.create(mRoomConfig);

	}

	/**
	 * Start a sign in activity.  To properly handle the result, call tryHandleSignInResult from
	 * your Activity's onActivityResult function
	 */
	public void startSignInIntent() {
		startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
	}

	private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback() {

		// Called when room has been created
		@Override
		public void onRoomCreated(int statusCode, Room room) {
			if (statusCode == GamesCallbackStatusCodes.OK && room != null) {
				Log.d(TAG, "Room " + room.getRoomId() + " created.");
				// save room ID so we can leave cleanly before the game starts.
				mRoomId = room.getRoomId();
				//TODO show the waiting room UI
				//showWaitingRoom(room);
			} else {
				Log.w(TAG, "Error creating room: " + statusCode);
			}
		}

		// Called when room is fully connected.
		@Override
		public void onRoomConnected(int statusCode, Room room) {
			debugLog("onRoomConnected(" + statusCode + ", " + room + ")");
			if (statusCode != GamesCallbackStatusCodes.OK) {
				debugLog("*** Error: onRoomConnected, status " + statusCode);
				//showGameError();
				return;
			}
			updateRoom(room);
		}

		// Called when this player has joined the room
		@Override
		public void onJoinedRoom(int statusCode, Room room) {
			debugLog("onJoinedRoom(" + statusCode + ", " + room + ")");
			if (statusCode != GamesCallbackStatusCodes.OK) {
				debugLog("*** Error: onRoomConnected, status " + statusCode);
				//showGameError();
				return;
			}
			mState.showWaitingRoom();
		}

		// Called when we've successfully left the room (this happens a result of voluntarily
		// leaving via a call to leaveRoom(). If we get disconnected, we get
		// onDisconnectedFromRoom()).
		@Override
		public void onLeftRoom(int statusCode, @NonNull String roomId) {
			// we have left the room; return to main screen.
			debugLog("onLeftRoom, code " + statusCode);

			mState.abortWaitingRoom();
		}
	};

	void updateRoom(Room room) {
		if (room != null) {
			mParticipants = room.getParticipants();
		}
		if (mParticipants != null) {
			//updatePeerScoresDisplay();
		}
		if (mPlaying) {
			// add new player to an ongoing game
			debugLog("Game already in progress...");
		} else if (shouldStartGame(room)) {
			// start game! TODO fix this mess!
			mPlaying = true;
			debugLog("GAME SHOULD START!!!");
			// TODO: This is a terrible solution!!!
			this.mState.startGame();
		}
	}

	OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener =
			new OnRealTimeMessageReceivedListener() {
		public int serialNumber = -1;

		@Override
		public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
			byte[] buf = realTimeMessage.getMessageData();
			String sender = realTimeMessage.getSenderParticipantId();
			// Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);

			// If the message is an unreliable position update
			if (buf[0] == 'P') {
				int serialNumber = byteArrayToInt(Arrays.copyOfRange(buf,1,5));
				if (serialNumber > this.serialNumber){
					int xPos 		 = byteArrayToInt(Arrays.copyOfRange(buf,5,9));
					int yPos 		 = byteArrayToInt(Arrays.copyOfRange(buf,9,13));
					int dir			 = (int) buf[13];
					if (playState != null){
						playState.receivePosition(xPos,yPos, dir);
					}

					this.serialNumber = serialNumber;
				}

			}
            // If it is damage to a tile call PlayState
            if ((char) buf[0] == 'D') {
                int tileX = byteArrayToInt(Arrays.copyOfRange(buf,1,5));
                int tileY = byteArrayToInt(Arrays.copyOfRange(buf,5,9));
                if (playState != null){
					playState.receiveDamageToTile(tileX,tileY);
				}
            }
        }
	};

	// returns whether there are enough players to start the game
	boolean shouldStartGame(Room room) {
		int connectedPlayers = 0;
		for (Participant p : room.getParticipants()) {
			if (p.isConnectedToRoom()) {
				++connectedPlayers;
			}
		}
		return connectedPlayers >= MIN_PLAYERS;
	}

	// returns true if the there is a game in progress
	void shouldCancelGame(){
		if (mPlaying){
			mPlaying = false;
			this.playState.cancelGame();
		}
	}
	private Activity thisActivity = this;
	private RoomStatusUpdateCallback mRoomStatusUpdateCallback = new RoomStatusUpdateCallback() {
		// Called when we are connected to the room. We're not ready to play yet! (maybe not
		// everybody is connected yet).
		@Override
		public void onConnectedToRoom(Room room) {
			Log.d(TAG, "onConnectedToRoom.");

			//get participants and my ID:
			mParticipants = room.getParticipants();
			mMyId = room.getParticipantId(mPlayerId);

			// save room ID if its not initialized in onRoomCreated() so we can leave cleanly
			// before the game starts.
			if (mRoomId == null) {
				mRoomId = room.getRoomId();
			}

			// print out the list of participants (for debug purposes)
			Log.d(TAG, "Room ID: " + mRoomId);
			Log.d(TAG, "My ID " + mMyId);
			Log.d(TAG, "<< CONNECTED TO ROOM>>");
		}

		// Called when we get disconnected from the room. We return to the main screen.
		@Override
		public void onDisconnectedFromRoom(Room room) {
			mRoomId = null;
			mRoomConfig = null;
			//showGameError();
			// TODO: go to main screen
			shouldCancelGame();
		}


		// We treat most of the room update callbacks in the same way: we update our list of
		// participants and update the display. In a real game we would also have to check if that
		// change requires some action like removing the corresponding player avatar from the
		// screen, etc.
		@Override
		public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
			updateRoom(room);
			shouldCancelGame();
		}

		@Override
		public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
			updateRoom(room);
		}

		@Override
		public void onP2PDisconnected(@NonNull String participant) {
		}

		@Override
		public void onP2PConnected(@NonNull String participant) {
		}

		@Override
		public void onPeerJoined(Room room, @NonNull List<String> arg1) {
			updateRoom(room);
		}

		@Override
		public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {
			leaveRoom();
			updateRoom(room);
			shouldCancelGame();
		}

		@Override
		public void onRoomAutoMatching(Room room) {
			updateRoom(room);
		}

		@Override
		public void onRoomConnecting(Room room) {
			updateRoom(room);
		}

		@Override
		public void onPeersConnected(Room room, @NonNull List<String> peers) {
			updateRoom(room);
		}

		@Override
		public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
			leaveRoom();
			updateRoom(room);
			shouldCancelGame();
		}
	};

	/**
	 * Try to sign in without displaying dialogs to the user.
	 * <p>
	 * If the user has already signed in previously, it will not show dialog.
	 */
	public void signInSilently() {
		Log.d(TAG, "signInSilently()");
		if (mGoogleSignInClient != null){
			Log.d(TAG, mGoogleSignInClient.toString());
		} else {
			Log.d(TAG, "mGoogleSignInClient is null :(");
		}


		mGoogleSignInClient.silentSignIn().addOnCompleteListener( this,
				new OnCompleteListener<GoogleSignInAccount>() {
					@Override
					public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
						if (task.isSuccessful()) {
							Log.d(TAG, "signInSilently(): success");
							onConnected(task.getResult());
						} else {
							Log.d(TAG, "signInSilently(): failure", task.getException());
							//onDisconnected();
						}
					}
				});
	}

	public void onDisconnected() {
		Log.d(TAG, "onDisconnected()");

		mRealTimeMultiplayerClient = null;
		//mInvitationsClient = null;

		//switchToMainScreen();
	}

	private void onConnected(GoogleSignInAccount googleSignInAccount) {
		Log.d(TAG, "onConnected(): connected to Google APIs");
		if (mSignedInAccount != googleSignInAccount) {

			mSignedInAccount = googleSignInAccount;

			// update the clients
			mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this,
																			googleSignInAccount);
			//mInvitationsClient = Games.getInvitationsClient(MainActivity.this,
			// googleSignInAccount);

			// get the playerId from the PlayersClient
			PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
			playersClient.getCurrentPlayer()
					.addOnSuccessListener(new OnSuccessListener<Player>() {
						@Override
						public void onSuccess(Player player) {
							mPlayerId = player.getPlayerId();
							debugLog("moe debug - mPlayerId: " + mPlayerId);
							//switchToMainScreen();
						}
					})
					.addOnFailureListener(createFailureListener(
							"There was a problem getting the player id!"));
		}
	}

	private OnFailureListener createFailureListener(final String string) {
		return new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				// handleException(e, string);
				debugLog(string);
			}
		};
	}

	void debugLog(String message) {

		Log.d(TAG, "GameHelper: " + message);

	}

	// Leave the room.
    @Override
    public void leaveRoom() {
		Log.d(TAG, "Leaving room.");
		//mSecondsLeft = 0;
		//stopKeepingScreenOn();
		if (mRoomId != null) {
			mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							mRoomId = null;
							mRoomConfig = null;
						}
					});
			//switchToScreen(R.id.screen_wait);
			// TODO: switchToMainScreen();
		} else {
			// TODO: switchToMainScreen();
		}
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "**** got onStop");

		// if we're in a room, leave it.
		leaveRoom();

		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");

		// Since the state of the signed in user can change when the activity is not active
		// it is recommended to try and sign in silently from when the app resumes.
		signInSilently();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		// gameHelper.onActivityResult(requestCode, resultCode, data);
	}
	*/


	@Override
	public void signIn()
	{
		debugLog("--- startSignInIntent()");
		startSignInIntent();
	}

	@Override
	public void signOut() {
		Log.d(TAG, "signOut()");

		mGoogleSignInClient.signOut().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {

						if (task.isSuccessful()) {
							Log.d(TAG, "signOut(): success");
						} else {
							handleException(task.getException(), "signOut() failed!");
						}

						onDisconnected();
					}
				});
	}

	@Override
	public boolean isSignedIn() {
		return false;
	}



	@Override
	public void registerGameState(PlayState pstate) {
		this.playState = pstate;
	}

	@Override
	public void sendReliableMessage(byte[] message) {
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId)) {
				continue;
			}

		mRealTimeMultiplayerClient.sendReliableMessage(message,
				mRoomId, p.getParticipantId(),
				new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
					@Override
					public void onRealTimeMessageSent(int statusCode,
													  int tokenId, String recipientParticipantId) {
						Log.d(TAG, "RealTime message sent");
						Log.d(TAG, "  statusCode: " + statusCode);
						Log.d(TAG, "  tokenId: " + tokenId);
						Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
					}
				})
				.addOnSuccessListener(new OnSuccessListener<Integer>() {
					@Override
					public void onSuccess(Integer tokenId) {
						Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
					}
				});
		}
	}

	@Override
	public void sendUnreliableMessage(byte[] message) {
		for (Participant p : mParticipants) {
			if (p.getParticipantId().equals(mMyId)) {
				continue;
			}
			mRealTimeMultiplayerClient.sendUnreliableMessage(message, mRoomId,
					p.getParticipantId());
		}
	}

	/**
	 * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
	 *
	 * @param exception The exception to evaluate.  Will try to display a more descriptive reason
	 *                  for the exception.
	 * @param details   Will display alongside the exception if you wish to provide more details
	 *                  for why the exception happened
	 */
	private void handleException(Exception exception, String details) {
		int status = 0;

		if (exception instanceof ApiException) {
			ApiException apiException = (ApiException) exception;
			status = apiException.getStatusCode();
		}

		String errorString = null;
		switch (status) {
			case GamesCallbackStatusCodes.OK:
				break;
			case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
				errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
				break;
			case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
				errorString = getString(R.string.match_error_already_rematched);
				break;
			case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
				errorString = getString(R.string.network_error_operation_failed);
				break;
			case GamesClientStatusCodes.INTERNAL_ERROR:
				errorString = getString(R.string.internal_error);
				break;
			case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
				errorString = getString(R.string.match_error_inactive_match);
				break;
			case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
				errorString = getString(R.string.match_error_locally_modified);
				break;
			default:
				errorString = getString(R.string.unexpected_status,
										GamesClientStatusCodes.getStatusCodeString(status));
				break;
		}

		if (errorString == null) {
			return;
		}

		String message = getString(R.string.status_exception_error, details, status, exception);

		new AlertDialog.Builder(AndroidLauncher.this)
				.setTitle("Error")
				.setMessage(message + "\n" + errorString)
				.setNeutralButton(android.R.string.ok, null)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == RC_SIGN_IN) {

			Task<GoogleSignInAccount> task =
					GoogleSignIn.getSignedInAccountFromIntent(intent);

			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				onConnected(account);
				debugLog("Manual login success");
			} catch (ApiException apiException) {
				String message = apiException.getMessage();
				if (message == null || message.isEmpty()) {
					message = getString(R.string.signin_other_error);
				}

				onDisconnected();

				new AlertDialog.Builder(this)
						.setMessage(message)
						.setNeutralButton(android.R.string.ok, null)
						.show();
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public static int byteArrayToInt(byte[] b) {
		return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
	}
}
