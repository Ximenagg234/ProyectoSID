import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

interface Notificacion {
  id: string;
  mensaje: string;
  timestamp: number;
  leida: boolean;
}

interface NotificacionesState {
  lista: Notificacion[];
  noLeidas: number;
}

const notificacionesSlice = createSlice({
  name: 'notificaciones',
  initialState: { lista: [], noLeidas: 0 } as NotificacionesState,
  reducers: {
    agregarNotificacion: (state, action: PayloadAction<Omit<Notificacion, 'leida'>>) => {
      state.lista.unshift({ ...action.payload, leida: false });
      state.noLeidas += 1;
    },
    marcarTodasLeidas: (state) => {
      state.lista.forEach((n) => {
        n.leida = true;
      });
      state.noLeidas = 0;
    },
  },
});

export const { agregarNotificacion, marcarTodasLeidas } = notificacionesSlice.actions;
export default notificacionesSlice.reducer;
