package com.example.circlematch;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new GameView(this));
    }

    static class GameView extends View {
        Paint p=new Paint(3); Paint text=new Paint(3);
        float cx,cy,R; int moves=0; Ball selected=null;
        int[] colors={Color.rgb(255,64,95),Color.rgb(255,210,26),Color.rgb(41,227,91),Color.rgb(36,150,255),Color.rgb(244,247,251)};
        ArrayList<Ball> balls=new ArrayList<>();
        Random random=new Random();

        static class Ball { int ring,col; float a; Ball(int r,float a,int c){ring=r;this.a=a;col=c;} }

        GameView(Context c){ super(c); setFocusable(true); newGame(); }

        void newGame(){
            moves=0; selected=null; balls.clear();
            for(int ring=0;ring<3;ring++) for(int i=0;i<8;i++){
                float a=(float)(i/8.0*Math.PI*2 + ring*.55);
                int col=(i+ring*3)%colors.length;
                balls.add(new Ball(ring,a,col));
            }
            invalidate();
        }
        PointF ringCenter(int r){
            float ox=new float[]{-R*.42f,R*.42f,0}[r];
            float oy=new float[]{-R*.02f,-R*.02f,R*.34f}[r];
            return new PointF(cx+ox,cy+oy);
        }
        PointF pos(Ball b){
            PointF q=ringCenter(b.ring); float rr=R*.62f;
            return new PointF(q.x+(float)Math.cos(b.a)*rr,q.y+(float)Math.sin(b.a)*rr);
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c); cx=getWidth()/2f; cy=getHeight()*.52f; R=Math.min(getWidth(),getHeight())*.34f;
            LinearGradient g=new LinearGradient(0,0,getWidth(),getHeight(),Color.rgb(16,45,67),Color.rgb(6,19,29),Shader.TileMode.CLAMP);
            p.setShader(g); c.drawRect(0,0,getWidth(),getHeight(),p); p.setShader(null);

            text.setColor(Color.WHITE); text.setTextSize(24); text.setTypeface(Typeface.DEFAULT_BOLD);
            c.drawText("Circle Match",16,42,text);
            p.setColor(Color.rgb(16,42,67)); c.drawRoundRect(getWidth()-150,16,getWidth()-75,54,18,18,p);
            p.setColor(Color.rgb(33,77,118)); c.drawRoundRect(getWidth()-70,16,getWidth()-12,54,12,12,p);
            text.setTextSize(15); text.setColor(Color.WHITE); c.drawText("Moves: "+moves,getWidth()-142,41,text);
            text.setTextSize(13); c.drawText("नया",getWidth()-55,40,text);

            for(int ri=0;ri<3;ri++){
                PointF q=ringCenter(ri);
                p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(selected!=null&&selected.ring==ri?3:2);
                p.setColor(selected!=null&&selected.ring==ri?Color.rgb(255,195,43):Color.rgb(99,137,173));
                for(int k=0;k<3;k++) c.drawCircle(q.x,q.y,R*(.48f+k*.105f),p);
                p.setStyle(Paint.Style.FILL);
            }
            for(Ball b:balls){
                PointF q=pos(b); float rad=Math.max(8,Math.min(14,getWidth()*.022f));
                p.setColor(colors[b.col]); p.setShadowLayer(selected==b?18:8,0,0,colors[b.col]);
                setLayerType(View.LAYER_TYPE_SOFTWARE,p); c.drawCircle(q.x,q.y,rad+(selected==b?4:0),p); p.clearShadowLayer();
                if(selected==b){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setColor(Color.WHITE);c.drawCircle(q.x,q.y,rad+4,p);p.setStyle(Paint.Style.FILL);}
            }
            text.setTextSize(13); text.setColor(Color.WHITE);
            c.drawText("गेंद को टैप करें → दूसरे रिंग पर ले जाएँ",16,getHeight()-24,text);
        }
        Ball nearest(float x,float y){
            Ball best=null; float d0=28;
            for(Ball b:balls){PointF q=pos(b);float d=(float)Math.hypot(x-q.x,y-q.y);if(d<d0){d0=d;best=b;}}
            return best;
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            if(e.getAction()!=MotionEvent.ACTION_DOWN)return true;
            float x=e.getX(),y=e.getY();
            if(x>getWidth()-80&&y<70){newGame();return true;}
            Ball b=nearest(x,y); if(b==null)return true;
            if(selected==b){selected=null;invalidate();return true;}
            if(selected!=null){
                selected.ring=(selected.ring+1)%3; selected.a+=.18f; moves++;
                selected=null; checkWin(); invalidate();
            } else { selected=b; invalidate(); }
            return true;
        }
        void checkWin(){
            for(int col=0;col<colors.length;col++){
                int[] ct={0,0,0};
                for(Ball b:balls) if(b.col==col) ct[b.ring]++;
                int mx=Math.max(ct[0],Math.max(ct[1],ct[2]));
                if(mx<3)return;
            }
            new Handler().postDelayed(()->new AlertDialog.Builder(getContext())
                .setTitle("🎉 शानदार!")
                .setMessage("Level पूरा हुआ!\nMoves: "+moves)
                .setPositiveButton("नया गेम",(d,w)->newGame()).show(),100);
        }
    }
}
