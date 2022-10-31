import { createSlice } from '@reduxjs/toolkit';

type ModalInitialState = {
  eventModalIsOpen: boolean;
  detailModalIsOpen: boolean;
};

const initialState: ModalInitialState = {
  eventModalIsOpen: false,
  detailModalIsOpen: false,
};

const modalSlice = createSlice({
  name: 'modal',
  initialState,
  reducers: {
    setEventModalOpen: state => {
      state.eventModalIsOpen = !state.eventModalIsOpen;
    },
    setDetailModalOpen: state => {
      state.detailModalIsOpen = !state.detailModalIsOpen;
    },
  },
});

const { reducer } = modalSlice;
export const ModalSelector = (state:any) => state.modal;
export const { setEventModalOpen, setDetailModalOpen } = modalSlice.actions;
export default reducer;