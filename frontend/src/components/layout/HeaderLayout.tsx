import { Link } from 'react-router-dom';
import { useAppDispatch } from '../../stores/ConfigHooks';
import { setDeleteModalOpen } from '../../stores/modules/modal';
import LogoImage from '../../assets/logo_title.png';

function HeaderLayout() {
  const dispatch = useAppDispatch();

  const userId = window.localStorage.getItem('id');
  const nickname = window.localStorage.getItem('nickname');
  const role = window.localStorage.getItem('roleType');

  // DeleteModal에 logout 타입으로 사용됨을 전달 + 모달 온오프
  const handleLogoutModal = () => {
    dispatch(setDeleteModalOpen('logout'));
  };

  return (
    <div className="fixed flex items-center justify-between bg-[white] w-full h-l border-b-2 border-line">
      <div className="flex items-center cursor-pointer">
        {/* 로고 클릭 시 본인 달력으로 이동 */}
        <a href={`/calendar/${userId}`}>
          <img className="h-s ml-2" src={LogoImage} alt="logo" />
        </a>
        {/* 현재 버전 명시 - 클릭시 깃북으로 이동*/}
        <button
          onClick={() => window.open('https://meetup.gitbook.io/meetup-docs/release-notes', '_blank')}
          className="drop-shadow-shadow mt-1 border-placeholder bg-title text-background px-3 rounded h-[30px] font-bold ml-3 text-s font-dots pb-1"
        >
          v 2.0.4
        </button>
      </div>

      <div className="flex mr-2 items-center">
        <div className="font-script font-bold text-title mr-2 text-m mt-1 cursor-default">{role?.slice(5)}</div>
        <Link to="/settings">
          <div className="font-bold pr-1 underline underline-offset-2">{nickname}</div>
        </Link>
        <svg
          xmlns="https://www.w3.org/2000/svg"
          fill="none"
          strokeWidth="1.5"
          stroke="currentColor"
          className="w-6 h-6 pt-0.5 cursor-pointer"
          onClick={handleLogoutModal}
        >
          <path d="M15.75 9V5.25A2.25 2.25 0 0013.5 3h-6a2.25 2.25 0 00-2.25 2.25v13.5A2.25 2.25 0 007.5 21h6a2.25 2.25 0 002.25-2.25V15m3 0l3-3m0 0l-3-3m3 3H9" />
        </svg>
      </div>
    </div>
  );
}

export default HeaderLayout;
