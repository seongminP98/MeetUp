import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { tSchedule } from '../../types/events';
import { axiosInstance } from '../../components/auth/axiosConfig';
import { RootState } from '../ConfigStore';
import axios from 'axios';

type scheduleInitialState = {
  loading: boolean;
  schedules: {
    meetingFromMe: Array<tSchedule>;
    meetingToMe: Array<tSchedule>;
    scheduleResponseList: Array<tSchedule>;
  };
};

const initialState: scheduleInitialState = {
  loading: false,
  schedules: {
    meetingFromMe: [
      {
        id: '',
        start: '',
        end: '',
        title: '',
        content: '',
        userId: '',
        userName: '',
        meetupName: '',
        meetupColor: '',
      },
    ],
    meetingToMe: [
      {
        id: '',
        start: '',
        end: '',
        title: '',
        content: '',
        userId: '',
        userName: '',
        meetupName: '',
        meetupColor: '',
      },
    ],
    scheduleResponseList: [
      {
        id: '',
        start: '',
        end: '',
        title: '',
        content: '',
        userId: '',
        userName: '',
        meetupName: '',
        meetupColor: '',
      },
    ],
  },
};

export const fetchSchedule = createAsyncThunk('schedule/fetch', async (thunkAPI: any) => {
  try {
    const res = await axiosInstance.get(`/schedule?targetId=${thunkAPI[0]}&date=${thunkAPI[1]} 00:00:00`).then((res) => {
      console.log('my schedule fetched: ', res.data);
      return res.data;
    });
    return res;
  } catch (err) {
    console.log(err);
  }
});


export const addSchedule = createAsyncThunk('schedule/fetchAddSchedule', async(thunkAPI:any) => {
  console.log(thunkAPI)
  try {
    const res = await axiosInstance.post('/schedule',thunkAPI).then((res) => {
      console.log('schedule data created: ', res);
      return res.data;
    });
    return res.data;
  } catch(err) {
    console.log(err)
  }
});

export const addMeeting = createAsyncThunk('schedule/fetchAddMeeting', async(thunkAPI:any) => {
  console.log(thunkAPI)
  try {
    const res = await axiosInstance.post('/meeting ',thunkAPI).then((res) => {
      console.log('meeting data created: ', res);
      return res.data;
    });
    return res.data;
  } catch(err) {
    console.log(err)
  }
});



const scheduleSlice = createSlice({
  name: 'schedule',
  initialState,
  reducers: {},
  extraReducers: {
    // POST
    [addSchedule.pending.toString()]: (state) => {
      state.loading = false;
    },
    [addSchedule.fulfilled.toString()]: (state, action) => {
      state.loading = true;
    },
    [addSchedule.rejected.toString()]: (state) => {
      state.loading = false;
    },
    // POST
    [addMeeting.pending.toString()]: (state) => {
      state.loading = false;
    },
    [addMeeting.fulfilled.toString()]: (state, action) => {
      state.loading = true;
    },
    [addMeeting.rejected.toString()]: (state) => {
      state.loading = false;
    },
    // GET
    [fetchSchedule.pending.toString()]: (state) => {
      state.loading = false;
    },
    [fetchSchedule.fulfilled.toString()]: (state, action) => {
      state.loading = true;
      state.schedules = action.payload;
      console.log(state.schedules)
    },
    [fetchSchedule.rejected.toString()]: (state) => {
      state.loading = false;
    },
  },
});


const { reducer } = scheduleSlice;
export const scheduleSelector = (state:RootState) => state.schedules
export const myScheduleSelector = (state: RootState) => state.schedules.schedules.scheduleResponseList;
export const meetingToMeSelector = (state: RootState) => state.schedules.schedules.meetingToMe;
export const meetingFromMeSelector = (state: RootState) => state.schedules.schedules.meetingFromMe;

// const { reducer } = ScheduleModalSlice;

export default reducer;