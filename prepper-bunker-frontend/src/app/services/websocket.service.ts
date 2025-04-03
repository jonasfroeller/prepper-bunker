import { Inject, Injectable, PLATFORM_ID, Signal, WritableSignal, signal } from '@angular/core';
import { environment } from '../environment';
import { isPlatformBrowser } from '@angular/common';

export interface WebSocketMessage {
    type: 'CREATE' | 'UPDATE' | 'DELETE';
    resourceType: string;
    resourceId?: number;
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private socket: WebSocket | null = null;
    private _connected: WritableSignal<boolean> = signal(false);
    private _lastMessage: WritableSignal<WebSocketMessage | null> = signal(null);
    private isBrowser: boolean;

    connected: Signal<boolean> = this._connected.asReadonly();
    lastMessage: Signal<WebSocketMessage | null> = this._lastMessage.asReadonly();

    constructor(@Inject(PLATFORM_ID) platformId: Object) {
        this.isBrowser = isPlatformBrowser(platformId);
    }

    connect(): void {
        if (!this.isBrowser) {
            return;
        }

        if (this.socket) {
            return;
        }

        this.socket = new WebSocket(environment.wsUrl);

        this.socket.addEventListener('open', () => {
            this._connected.set(true);
        });

        this.socket.addEventListener('message', (event) => {
            try {
                const message = JSON.parse(event.data) as WebSocketMessage;
                this._lastMessage.set(message);
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        });

        this.socket.addEventListener('close', () => {
            this._connected.set(false);
            this.socket = null;
            // Attempt to reconnect after a delay
            setTimeout(() => this.connect(), 5000);
        });

        this.socket.addEventListener('error', (error) => {
            console.error('WebSocket error:', error);
            this.socket?.close();
        });
    }

    disconnect(): void {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
            this._connected.set(false);
        }
    }
}
