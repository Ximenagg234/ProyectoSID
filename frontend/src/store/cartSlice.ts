import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { ProductoResponse } from '../types/producto.types';

export interface CartItem {
  producto: ProductoResponse;
  cantidad: number;
}

interface CartState {
  items: CartItem[];
}

const initialState: CartState = {
  items: [],
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addItem: (state, action: PayloadAction<ProductoResponse>) => {
      const existing = state.items.find(
        (i) => i.producto.idProducto === action.payload.idProducto
      );
      if (existing) {
        existing.cantidad += 1;
      } else {
        state.items.push({ producto: action.payload, cantidad: 1 });
      }
    },
    removeItem: (state, action: PayloadAction<number>) => {
      state.items = state.items.filter(
        (i) => i.producto.idProducto !== action.payload
      );
    },
    updateCantidad: (
      state,
      action: PayloadAction<{ idProducto: number; cantidad: number }>
    ) => {
      const item = state.items.find(
        (i) => i.producto.idProducto === action.payload.idProducto
      );
      if (item) {
        item.cantidad = Math.max(1, action.payload.cantidad);
      }
    },
    clearCart: (state) => {
      state.items = [];
    },
  },
});

export const { addItem, removeItem, updateCantidad, clearCart } =
  cartSlice.actions;
export default cartSlice.reducer;
