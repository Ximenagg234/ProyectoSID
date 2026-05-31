import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useDispatch } from 'react-redux';
import { agregarNotificacion } from '../store/notificacionesSlice';
import { useAuth } from './useAuth';
import type { AppDispatch } from '../store';

export const useWebSocket = (): void => {
  const { token, isEmprendedor } = useAuth();
  const dispatch = useDispatch<AppDispatch>();
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!token || !isEmprendedor) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: () => {
        client.subscribe('/topic/notificaciones', (message) => {
          const data = JSON.parse(message.body) as { mensaje: string };
          dispatch(
            agregarNotificacion({
              id: Date.now().toString(),
              mensaje: data.mensaje,
              timestamp: Date.now(),
            })
          );
        });
      },
    });

    client.activate();
    clientRef.current = client;
    return () => {
      client.deactivate();
    };
  }, [token, isEmprendedor, dispatch]);
};
