import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface WebSocketMessage {
    type: 'CREATE' | 'UPDATE' | 'DELETE';
    resourceType: string;
    resourceId: number;
    timestamp: string;
}

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private socket: WebSocket | null = null;
    private connected = false;
    private reconnectInterval = 5000;
    private reconnectTimer: any = null;

    lastMessage = signal<WebSocketMessage | null>(null);

    connect(): void {
        if (this.connected) {
            return;
        }

        try {
            this.socket = new WebSocket(environment.wsUrl);
            
            this.socket.onopen = () => {
                console.log('WebSocket connection established');
                this.connected = true;
                if (this.reconnectTimer) {
                    clearTimeout(this.reconnectTimer);
                    this.reconnectTimer = null;
                }
            };
            
            this.socket.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    this.lastMessage.set(data);
                } catch (error) {
                    console.error('Error parsing WebSocket message:', error);
                }
            };
            
            this.socket.onclose = () => {
                console.log('WebSocket connection closed');
                this.connected = false;
                this.scheduleReconnect();
            };
            
            this.socket.onerror = (error) => {
                console.error('WebSocket error:', error);
                this.close();
            };
        } catch (error) {
            console.error('Error creating WebSocket:', error);
            this.scheduleReconnect();
        }
    }

    close(): void {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
        }
        this.connected = false;
    }

    private scheduleReconnect(): void {
        if (!this.reconnectTimer) {
            this.reconnectTimer = setTimeout(() => {
                this.reconnectTimer = null;
                this.connect();
            }, this.reconnectInterval);
        }
    }
}
