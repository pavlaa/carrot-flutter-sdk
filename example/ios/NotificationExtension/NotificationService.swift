//
//  NotificationService.swift
//  NotificationExtension
//
//  Created by Alexander Manzurov on 10.08.2023.
//

import UserNotifications
import CarrotSDK

class NotificationService: CarrotNotificationServiceExtension {
    override func setup() {
        self.domainIdentifier = "group.cq.flutterSdkExample"
    }
    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        super.didReceive(request, withContentHandler: contentHandler)
    }
}
