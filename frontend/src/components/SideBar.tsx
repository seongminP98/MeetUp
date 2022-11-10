import ChannelList from '../components/ChannelList';
import MeetupList from '../components/MeetupList';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Alert from '@mui/material/Alert';
import { useState, useEffect } from 'react';
import { useAppDispatch } from '../stores/ConfigHooks';
import { axiosInstance } from './auth/axiosConfig';
import CircularProgress from '@mui/material/CircularProgress';

function SideBar() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [syncChecked, setSyncChecked] = useState(false);
  const [isClicked, setIsClicked] = useState(false);

  useEffect(() => {
    checkRole();

    const timeId = setTimeout(() => {
      setSyncChecked(false);
    }, 3000);

    return () => {
      clearTimeout(timeId);
    };
  }, [syncChecked]);

  const syncRequest = async () => {
    setIsClicked(true);
    await axiosInstance.get('/meetup/sync').then((res) => {
      if (res.status === 201) {
        // console.log('동기화 완료', res);
        setSyncChecked(true);
        setIsClicked(false);
        navigate(`/calendar/${localStorage.getItem('id')}`);
      }
    });
  };

  const [isStudent, setIsStudent] = useState(false);

  const checkRole = () => {
    if (localStorage.getItem('roleType') === 'ROLE_Student') {
      setIsStudent(true);
    }
  };

  return (
    <div className="SideBar relative w-full pl-2 mt-[70px] min-w-[200px]">
      {isStudent ? <div /> : <ChannelList />}
      <MeetupList />

      <div className="relative h-1/8 top-[450px]">
        <button
          onClick={syncRequest}
          className="bg-title hover:bg-hover text-background rounded w-full h-s drop-shadow-button flex justify-center items-center space-x-2 absolute -bottom-[10vh]"
        >
          {!syncChecked && isClicked ? (
            <CircularProgress sx={{ color: 'white' }} size="1.5rem" />
          ) : (
            <div className="flex">
              {syncChecked ? (
                <svg
                  xmlns="https://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="1.5"
                  stroke="currentColor"
                  className="w-6 h-6 animate-bounce"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
                </svg>
              ) : (
                <div className="flex gap-x-1">
                  <svg
                    xmlns="https://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth="1.5"
                    stroke="currentColor"
                    className="w-5 h-5"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99"
                    />
                  </svg>
                  <span className="text-xs"> MatterMost와 동기화하기</span>
                </div>
              )}
            </div>
          )}
        </button>
      </div>
    </div>
  );
}

export default SideBar;
