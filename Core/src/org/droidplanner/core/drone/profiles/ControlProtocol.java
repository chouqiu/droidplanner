package org.droidplanner.core.drone.profiles;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.droidplanner.core.MAVLink.MavLinkManual;
import org.droidplanner.core.MAVLink.MavLinkParameters;
import org.droidplanner.core.drone.DroneInterfaces;
import org.droidplanner.core.drone.DroneInterfaces.DroneEventsType;
import org.droidplanner.core.drone.DroneInterfaces.Handler;
import org.droidplanner.core.drone.DroneInterfaces.OnDroneListener;
import org.droidplanner.core.drone.DroneVariable;
import org.droidplanner.core.model.Drone;
import org.droidplanner.core.parameters.Parameter;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.ardupilotmega.msg_param_value;

/**
 * Class to manage the communication of parameters to the MAV.
 * 
 * Should be initialized with a MAVLink Object, so the manager can send messages
 * via the MAV link. The function processMessage must be called with every new
 * MAV Message.
 * 
 */
public class ControlProtocol extends DroneVariable implements OnDroneListener {

	private final static String TAG = ControlProtocol.class.getSimpleName();

	private static final int TIMEOUT = 30;

	public short yaw, pitch, roll, throttle;

	public boolean touchReadyToSend;

	public Handler watchdog;
	public Runnable watchdogCallback = new Runnable() {
		@Override
		public void run() {
			try {
				// process stick movement，处理摇杆数据
				if(touchReadyToSend==true){
					//btSendBytes(Protocol.getSendData(Protocol.SET_4CON,
					//		Protocol.getCommandData(Protocol.SET_4CON)));
					//MavLinkManual.sendManualMessage(myDrone, yaw, pitch, roll, throttle);
					MavLinkManual.sendManualMessage(myDrone, throttle, yaw, pitch, roll);

					Log.i(TAG,"Thro: " +throttle +",yaw: " +yaw+ ",roll: " + roll +",pitch: "+ pitch);

					touchReadyToSend=false;
				}

				//跟新显示摇杆数据，update the joystick data
				//updateLogData(0);

				resetWatchdog();

			} catch (Exception e) {

			}
		}
	};

	public ControlProtocol(Drone myDrone, Handler handler) {
		super(myDrone);
		this.watchdog = handler;
		myDrone.addDroneListener(this);

		yaw = roll = pitch = throttle = 0;
		touchReadyToSend = false;

		watchdog.postDelayed(watchdogCallback, TIMEOUT);
	}

	private void resetWatchdog() {
		watchdog.removeCallbacks(watchdogCallback);
		watchdog.postDelayed(watchdogCallback, TIMEOUT);
	}

	private void killWatchdog() {
		watchdog.removeCallbacks(watchdogCallback);
	}

	@Override
	public void onDroneEvent(DroneEventsType event, Drone drone) {
		switch (event) {
		case HEARTBEAT_FIRST:
			break;
		case DISCONNECTED:
			//killWatchdog();
			break;
		case HEARTBEAT_TIMEOUT:
			break;
		default:
			break;

		}
	}

}
