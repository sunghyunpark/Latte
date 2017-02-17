package pushevent;

public class My_Article_More_BtnPushEvent
{
    /**
     * 내 아티클 삭제 버튼 탭 시 해당 포지션값을 넘겨줌
     */

    private int position;    //리사이클러뷰로부터 올때의 해당 아이템 position
    private String from;    //어디로부터 발생한 이벤트 인지..(timeline, lookaround, myplace 등등등

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public My_Article_More_BtnPushEvent(int position, String from){
        this.position = position;
        this.from = from;
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