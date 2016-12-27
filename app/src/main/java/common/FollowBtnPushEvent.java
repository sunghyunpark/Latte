package common;

public class FollowBtnPushEvent
{

    /**
     * 팔로우 버튼 탭 시 다이얼로그로부터 이벤트를 받음
     */
    private String uid;
    private String state;
    private int position;    //리사이클러뷰로부터 올때의 해당 아이템 position

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public FollowBtnPushEvent(String uid, String state, int position){
        this.uid = uid;
        this.state = state;
        this.position = position;
    }

}

//BusProvider.getInstance().register(this); -> 받는 액티비티에 선언(oncreate에 넣어줌)
/*@Override -> 받는 액티비티에 선언
protected void onDestroy() {
	// Always unregister when an object no longer should be on the bus.
	BusProvider.getInstance().unregister(this);
	super.onDestroy();

}

	@Subscribe
	public void FinishLoad(Register_ProfilePushEvent mPushEvent) {
		Toast.makeText(getApplicationContext(), "오또성공", Toast.LENGTH_SHORT).show();
		club_intro = mPushEvent.getList();

// 이벤트가 발생한뒤 수행할 작업

	}*/

//BusProvider.getInstance().post(new Register_ProfilePushEvent(intro_text)); -> 이벤트를 보내는 엑티비티에 선언