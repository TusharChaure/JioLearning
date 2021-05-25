package com.example.jiolearningproject;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT_MEDIA_URI = "https://media.specialolympics.org/resources/video/Champions-Together-Youth-Video-2.5min.mp4?_ga=2.79042065.46941699.1621962741-1588985075.1621962741";
    private static ArrayList<String> Aduri = new ArrayList<>();
    private static ArrayList<String> Adid = new ArrayList<>();
    private static ArrayList<Integer> timeOffset = new ArrayList<>();
    private static ArrayList<String> temp = new ArrayList<>();
    private static int sum = 0;
    private static int counter = 0;
    @Nullable private PlayerView playerView;
    @Nullable private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.idExoPlayerVIew);
        Context context = getApplicationContext();
        player = new SimpleExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        try {
            vmap_parsing();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        vast_parsing();

        initializePlayer();

    }

    public void vmap_parsing() throws ParserConfigurationException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;
        try {

            URL url = new URL("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=");
            doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            printNode( doc.getChildNodes() );

        } catch (Exception e) {
            Log.e("LOSS", "error" + e);
        }

        for (int i = 0; i < Adid.size(); i++) {
            while (i < Adid.size() - 1 && Adid.get(i).matches(Adid.get(i+1)))
                i++;
            sum++;
        }

        NodeList nodes = doc.getElementsByTagName("vmap:AdSource");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            NodeList title = element.getElementsByTagName("vmap:AdTagURI");
            Element line = (Element) title.item(0);
            Aduri.add(getCharacterDataFromElement(line));
        }

    }

    private void vast_parsing() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        for(int i=0;i<Aduri.size();i++){
            try {
                URL url = new URL(Aduri.get(i));
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                printNode1( doc.getChildNodes(), i);

            } catch (Exception e) {
                Log.e("LOSS", "error" + e);
            }
        }

    }

    public static String getCharacterDataFromElement(Element e) {

        NodeList list = e.getChildNodes();
        String data;

        for(int index = 0; index < list.getLength(); index++){
            if(list.item(index) instanceof CharacterData){
                CharacterData child = (CharacterData) list.item(index);
                data = child.getData();

                if(data != null && data.trim().length() > 0)
                    return child.getData();
            }
        }
        return "";
    }

    private static void printNode(NodeList nList) {

        for(int i=0;i<nList.getLength();i++) {
            Node node = nList.item(i);

            if(node.hasChildNodes()) {
                if(node.getNodeName().matches("vmap:AdBreak")) {
                    if(node.hasAttributes()) {
                        if (node.getAttributes().getNamedItem( "timeOffset" ).getNodeValue().matches("start")){
                            timeOffset.add(0_000);
                        }
                        else if (node.getAttributes().getNamedItem( "timeOffset" ).getNodeValue().matches("end")){
                            timeOffset.add(-1);
                        }
                        else{
                            int time = Integer.parseInt(node.getAttributes().getNamedItem( "timeOffset" ).getNodeValue().replaceAll("[\\D]", ""));
                            if(time >= 100000){
                                time = time - 40000;
                                timeOffset.add(time);
                            }
                            else
                                timeOffset.add(Integer.parseInt(node.getAttributes().getNamedItem( "timeOffset" ).getNodeValue().replaceAll("[\\D]", "")));

                        }
                        Adid.add(node.getAttributes().getNamedItem( "breakId" ).getNodeValue());
                    }
                }
                printNode(node.getChildNodes());
            }
        }
    }

    private static void printNode1(NodeList nList, int j) {

        for(int i=0;i<nList.getLength();i++) {
            Node node = nList.item(i);
            if(node.hasChildNodes()) {
                if(node.getNodeName().matches("MediaFile")) {
                    if(node.hasAttributes()) {
                        if (node.getAttributes().getNamedItem( "type" ).getNodeValue().matches("video/mp4")) {
                            if (temp.size() == j)
                                temp.add(node.getTextContent());
                        }
                    }
                }
                printNode1(node.getChildNodes(), j);
            }
        }
    }

    private void initializePlayer() {

                int i;
                int k =1;
                int x = Collections.frequency(Adid, Adid.get(0));
                int z = Collections.frequency(Adid, Adid.get(Adid.size()-1));

                for(i=0;i<x;i++){
                    if(Adid.get(counter).matches("preroll")){
                        MediaItem preRollAd =
                                new MediaItem.Builder()
                                        .setUri(temp.get(counter))
                                        .setClipStartPositionMs(timeOffset.get(counter))
                                        .build();
                        player.addMediaItem(preRollAd);
                        counter++;
                    }
                }

                MediaItem contentStart =
                        new MediaItem.Builder()
                                .setUri(DEFAULT_MEDIA_URI)
                                .setClipEndPositionMs(timeOffset.get(counter))
                                .build();
                player.addMediaItem(contentStart);

                for(i=i;i<sum+x-1;i++){
                    if(Adid.get(counter).matches( "midroll-" + k )) {
                        int n = Collections.frequency(Adid, Adid.get(counter));
                        while(n>0){
                            MediaItem mid =
                                    new MediaItem.Builder()
                                            .setUri(temp.get(counter))
                                            .build();
                            player.addMediaItem(mid);
                            counter++;
                            n--;
                        }
                        int m = counter;
                        counter = counter -1;
                        int y =timeOffset.get(m);
                        if(y!=-1){
                            MediaItem contentEnd =
                                    new MediaItem.Builder()
                                            .setUri(DEFAULT_MEDIA_URI)
                                            .setClipStartPositionMs(timeOffset.get(counter))
                                            .setClipEndPositionMs(timeOffset.get(m))
                                            .build();
                            player.addMediaItem(contentEnd);
                            counter++;
                        }
                        else{
                            MediaItem contentEnd =
                                    new MediaItem.Builder()
                                            .setUri(DEFAULT_MEDIA_URI)
                                            .setClipStartPositionMs(timeOffset.get(counter))
                                            .build();
                            player.addMediaItem(contentEnd);
                            counter++;
                        }
                    }
                    k++;
                }

                for(int j=0;j<z;j++) {
                    if (Adid.get(Adid.size() - 1).matches("postroll")) {
                        MediaItem post =
                                new MediaItem.Builder()
                                        .setUri(temp.get(counter))
                                        .build();
                        player.addMediaItem(post);
                        i++;
                    }
                }

        player.prepare();
        player.play();

    }
}