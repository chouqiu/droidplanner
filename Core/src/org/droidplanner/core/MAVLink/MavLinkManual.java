package org.droidplanner.core.MAVLink;

import org.droidplanner.core.model.Drone;

import com.MAVLink.Messages.ardupilotmega.msg_manual_control;

public class MavLinkManual {

	public static void sendManualMessage(Drone drone, short x, short y, short z, short r) {
		msg_manual_control msg = new msg_manual_control();
		msg.target = 1;
		msg.buttons = 0;

		msg.x = x;
		msg.y = y;
		msg.z = z;
		msg.r = r;
		drone.getMavClient().sendMavPacket(msg.pack());
	}

}