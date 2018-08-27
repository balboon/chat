package com.spontivly.chat.localDb;

import com.spontivly.chat.localDb.entity.LocalEventChat;
import com.spontivly.chat.localDb.entity.LocalEvents;
import com.spontivly.chat.localDb.entity.LocalUsers;

public class LocalDbUtils {

    public LocalDbUtils(){}

    public static boolean compareEventchat(LocalEventChat ec1, LocalEventChat ec2) {
        if (ec1.eventId != ec2.eventId)
            return false;
        if (ec1.createdAt.getTime() != ec2.createdAt.getTime())
            return false;
        if (ec1.posterId != ec2.posterId)
            return false;
        if (!ec1.postedMessage.equals(ec2.postedMessage))
            return false;
        if (!ec1.posterLastName.equals(ec2.posterLastName))
            return false;
        if (!ec1.posterFirstName.equals(ec2.posterFirstName))
            return false;

        return true;
    }

    public static boolean compareEvents(LocalEvents ev1, LocalEvents ev2) {
        if (ev1.eventId != ev2.eventId)
            return false;
        if (ev1.typeId != ev2.typeId)
            return false;
        if (ev1.ownerId != ev2.ownerId)
            return false;
        if (ev1.showOwnerPhone != ev2.showOwnerPhone)
            return false;
        if (!ev1.title.equals(ev2.title))
            return false;
        if (!ev1.description.equals(ev2.description))
            return false;
        if (!ev1.address.equals(ev2.address))
            return false;
        if (ev1.lat != ev2.lat)
            return false;
        if (ev1.lng != ev2.lng)
            return false;
        if (ev1.roughLat != ev2.roughLat)
            return false;
        if (ev1.roughLng != ev2.roughLng)
            return false;
        if (ev1.startTime.getTime() != ev2.startTime.getTime())
            return false;
        if (ev1.endTime.getTime() != ev2.endTime.getTime())
            return false;
        if (ev1.hasOwnerArrived != ev2.hasOwnerArrived)
            return false;
        if (ev1.maxUsers != ev2.maxUsers)
            return false;
        if (ev1.ispublic != ev2.ispublic)
            return false;
        if (ev1.isVisible != ev2.isVisible)
            return false;
        if (ev1.isArchived != ev2.isArchived)
            return false;
        if (ev1.joined != ev2.joined)
            return false;

        return true;
    }

    public static boolean compareUsers(LocalUsers usr1, LocalUsers usr2) {
        if (!usr1.firstName.equals(usr2.firstName))
            return false;
        if (!usr1.lastName.equals(usr2.lastName))
            return false;
        if (usr1.userId != usr2.userId)
            return false;

        return true;
    }
}
