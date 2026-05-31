import { configureStore } from '@reduxjs/toolkit';
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from 'redux-persist';
import { combineReducers } from 'redux';
import authReducer from './authSlice';
import notificacionesReducer from './notificacionesSlice';
import cartReducer from './cartSlice';

// Wrapper manual de localStorage — evita el problema de CJS con Vite 8 rolldown
const localStorageEngine = {
  getItem: (key: string): Promise<string | null> =>
    Promise.resolve(localStorage.getItem(key)),
  setItem: (key: string, value: string): Promise<void> =>
    Promise.resolve(localStorage.setItem(key, value)),
  removeItem: (key: string): Promise<void> =>
    Promise.resolve(localStorage.removeItem(key)),
};

const rootReducer = combineReducers({
  auth: authReducer,
  notificaciones: notificacionesReducer,
  cart: cartReducer,
});

const persistedReducer = persistReducer(
  { key: 'root', storage: localStorageEngine, whitelist: ['auth'] },
  rootReducer
);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

export const persistor = persistStore(store);
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
