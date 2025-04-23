// websocket.js
let stompClient = null;
let userId = null;
let onNotificationCallback = null;

function connectWebSocket(currentUserId, callback) {
    userId = currentUserId;
    onNotificationCallback = callback;

    // Don't connect if already connected
    if (stompClient && stompClient.connected) {
        return;
    }

    // Use the correct URL to connect to memberservice
    const socket = new SockJS('http://localhost:8081/ws');
    stompClient = Stomp.over(socket);

    // Connect without logging
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket');

        // Subscribe to personal notifications
        stompClient.subscribe('/user/' + userId + '/queue/notifications', function(message) {
            const notification = JSON.parse(message.body);
            console.log('Received notification:', notification);

            // Call the callback function with the notification
            if (onNotificationCallback) {
                onNotificationCallback(notification);
            }

            // Handle different notification types
            handleNotification(notification);
        });

        // Send status update (online)
        sendStatusUpdate(1);
    }, function(error) {
        console.error('Error connecting to WebSocket:', error);

        // Try to reconnect after 5 seconds
        setTimeout(function() {
            connectWebSocket(userId, onNotificationCallback);
        }, 5000);
    });
}

function disconnect() {
    if (stompClient) {
        // Send status update (offline) before disconnecting
        sendStatusUpdate(0);

        stompClient.disconnect(function() {
            console.log('Disconnected from WebSocket');
        });
    }
}

function sendStatusUpdate(status) {
    if (stompClient && stompClient.connected && userId) {
        stompClient.send("/app/update-status", {},
            JSON.stringify({
                userId: userId,
                status: status
            })
        );
    }
}

function sendFriendRequest(receiverId) {
    if (stompClient && stompClient.connected && userId) {
        stompClient.send("/app/friend-request", {},
            JSON.stringify({
                requesterId: userId,
                receiverId: receiverId
            })
        );
        return true;
    }
    return false;
}

function respondToFriendRequest(invitationId, status) {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/friend-response", {},
            JSON.stringify({
                invitationId: invitationId,
                status: status  // "ACCEPTED" or "REJECTED"
            })
        );
        return true;
    }
    return false;
}

function handleNotification(notification) {
    switch(notification.type) {
        case 'FRIEND_REQUEST':
            showFriendRequestNotification(notification);
            break;
        case 'FRIEND_REQUEST_RESPONSE':
            showFriendResponseNotification(notification);
            break;
        case 'FRIEND_STATUS_CHANGE':
            updateFriendStatus(notification);
            break;
    }
}

function showFriendRequestNotification(notification) {
    // This is just an example - implement based on your UI
    const requester = notification.requester;

    // Create notification element
    const notificationElement = document.createElement('div');
    notificationElement.className = 'friend-request-notification';
    notificationElement.innerHTML = `
        <p><strong>${requester.username}</strong> sent you a friend request</p>
        <div class="notification-actions">
            <button onclick="acceptFriendRequest(${notification.invitationId})">Accept</button>
            <button onclick="rejectFriendRequest(${notification.invitationId})">Reject</button>
        </div>
    `;

    // Append to notifications container (implement based on your UI)
    const notificationsContainer = document.getElementById('notifications-container');
    if (notificationsContainer) {
        notificationsContainer.appendChild(notificationElement);
    }

    // You could also show a browser notification
    if (Notification.permission === 'granted') {
        new Notification('New Friend Request', {
            body: `${requester.username} sent you a friend request`
        });
    }
}

function showFriendResponseNotification(notification) {
    const responder = notification.responder;
    const status = notification.status;

    let message = '';
    if (status === 'ACCEPTED') {
        message = `${responder.username} accepted your friend request!`;
    } else {
        message = `${responder.username} declined your friend request.`;
    }

    // Create notification element
    const notificationElement = document.createElement('div');
    notificationElement.className = 'friend-response-notification';
    notificationElement.innerHTML = `<p>${message}</p>`;

    // Append to notifications container
    const notificationsContainer = document.getElementById('notifications-container');
    if (notificationsContainer) {
        notificationsContainer.appendChild(notificationElement);
    }

    // Browser notification
    if (Notification.permission === 'granted') {
        new Notification('Friend Request Response', {
            body: message
        });
    }

    // If accepted, update the friends list
    if (status === 'ACCEPTED') {
        // Refresh friends list if on the appropriate page
        if (typeof refreshFriendsList === 'function') {
            refreshFriendsList();
        }
    }
}

function updateFriendStatus(notification) {
    // Update the status of a friend in the UI
    const friendElements = document.querySelectorAll(`[data-friend-id="${notification.userId}"]`);

    friendElements.forEach(element => {
        const statusElement = element.querySelector('.friend-status');
        if (statusElement) {
            statusElement.className = `friend-status ${notification.status === 1 ? 'online' : 'offline'}`;
            statusElement.textContent = notification.status === 1 ? 'Online' : 'Offline';
        }
    });
}

// Helper functions for notification actions
function acceptFriendRequest(invitationId) {
    respondToFriendRequest(invitationId, 'ACCEPTED');

    // Remove notification from UI
    removeNotificationFromUI(invitationId);
}

function rejectFriendRequest(invitationId) {
    respondToFriendRequest(invitationId, 'REJECTED');

    // Remove notification from UI
    removeNotificationFromUI(invitationId);
}

function removeNotificationFromUI(invitationId) {
    // This would depend on your UI implementation
    const notificationElement = document.querySelector(`.friend-request-notification[data-invitation-id="${invitationId}"]`);
    if (notificationElement) {
        notificationElement.remove();
    }
}

// Request browser notification permission
function requestNotificationPermission() {
    if (Notification && Notification.permission !== "granted") {
        Notification.requestPermission();
    }
}

// Export functions for global use
window.WebSocketService = {
    connect: connectWebSocket,
    disconnect: disconnect,
    sendFriendRequest: sendFriendRequest,
    respondToFriendRequest: respondToFriendRequest,
    requestNotificationPermission: requestNotificationPermission
};