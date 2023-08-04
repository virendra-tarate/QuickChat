//Warning
// Unauthorized use, reproduction, or distribution of this code, in whole or in part, without the explicit permission of the owner, is strictly prohibited and may result in severe legal consequences under the relevant IT Act and other applicable laws.
// To use this code, you must first obtain written permission from the owner. For inquiries regarding licensing, collaboration, or any other use of the code, please contact virendratarte22@gmail.com.
// Thank you for respecting the intellectual property rights of the owner.
package com.a.v.virendra.tarate.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageActivity extends AppCompatActivity {

    private static final String EncryptionKEY = "VRT0JST0PRS0VVT1"; // key i.e. 128bit

    private RecyclerView recyclerView;
    private EditText edtMessageInput;
    private TextView txtChattingWith,chattingWithMobile;
    private ProgressBar progressBar;
    private ImageView imgToolbar,imgSend;
    Dialog dialog;
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    TextView timer;


    String usernameOfTheRoommate,emailOfRoommate,chatRoomId,phoneNum;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //removing action bar
        try {
            this.getSupportActionBar().hide();
        }catch(Exception e){

        }

        //getting information from intenet from Friends Activity
        usernameOfTheRoommate = getIntent().getStringExtra("username_of_roommate");
        emailOfRoommate = getIntent().getStringExtra("email_of_roommate");
        phoneNum = getIntent().getStringExtra("phoneNum");

        //Toast.makeText(MessageActivity.this, phoneNum, Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.messageRecycler);
        edtMessageInput = findViewById(R.id.editText);
        txtChattingWith = findViewById(R.id.chattingPerson);
        progressBar = findViewById(R.id.messageProgress);
        imgToolbar = findViewById(R.id.toolbar_image);
        imgSend = findViewById(R.id.sendMessageImage);
        chattingWithMobile = findViewById(R.id.mobile);
        //dialog
        dialog = new Dialog(MessageActivity.this);
        dialog.setContentView(R.layout.warningmsg);
        dialog.setCancelable(false);
        timer = dialog.findViewById(R.id.timer);


        //set roommate name and email
        txtChattingWith.setText(usernameOfTheRoommate);
        chattingWithMobile.setText("+91"+phoneNum);

        messages = new ArrayList<>();

        //sending messsage after clicking sen img
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isViolent = cheackValue();
                if(isViolent){
                    //dialog pop up
                    dialog.show();
                    //dismiss dialog after 7 sec
                    //7 sec delay
                    dialog.show();
                    new CountDownTimer(8000,1000){
                        int counter;
                        NumberFormat f = new DecimalFormat("0");
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long sec = (millisUntilFinished/1000)%60;
                            timer.setText(String.valueOf(f.format(sec)));
                            counter++;

                            if(sec == 0){
                                dialog.dismiss();
                            }

                        }

                        @Override
                        public void onFinish() {
                            //after 8 sec
                        }
                    }.start();

                    edtMessageInput.setText("");

                }
                else {

                    //AES Method To Encrypt the Message
                    FirebaseDatabase.getInstance().getReference("messages/" + chatRoomId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString(), "+91" + phoneNum, AESencryption(edtMessageInput.getText().toString())));
                    edtMessageInput.setText("");
                }
            }
        });

        messageAdapter = new MessageAdapter(messages,getIntent().getStringExtra("my_img"),getIntent().getStringExtra("img_of_roommate"), MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("img_of_roommate")).placeholder(R.drawable.account_img).error(R.drawable.account_img).into(imgToolbar);

        setUpChatRoom();



    }

    //cheack message
    boolean cheackValue(){

        //assigning value
        Boolean isBad = false;

        // Define the list of spam words and phrases
        String[] badWords = {"abuse", "bullshit", "fuck", "shit", "asshole", "bastard", "bitch", "cock", "cunt", "dick", "douche", "faggot", "jerk", "pussy", "slut", "whore","Mother Fucker","2g1c", "2 girls 1 cup", "acrotomophilia", "alabama hot pocket", "alaskan pipeline", "anal", "anilingus", "anus", "apeshit", "arsehole", "ass", "asshole", "assmunch", "auto erotic", "autoerotic", "babeland", "baby batter", "baby juice", "ball gag", "ball gravy", "ball kicking", "ball licking", "ball sack", "ball sucking", "bangbros", "bareback", "barely legal", "barenaked", "bastard", "bastardo", "bastinado", "bbw", "bdsm", "beaner", "beaners", "beaver cleaver", "beaver lips", "bestiality", "big black", "big breasts", "big knockers", "big tits", "bimbos", "birdlock", "bitch", "bitches", "black cock", "blonde action", "blonde on blonde action", "blow j", "blow job", "blow your l", "blue waffle", "blumpkin", "bollocks", "bondage", "boner", "boob", "boobs", "booty call", "brown showers", "brunette action", "bukkake", "bulldyke", "bullet vibe", "bullshit", "bung hole", "bunghole", "busty", "butt", "buttcheeks", "butthole", "camel toe", "camgirl", "camslut", "camwhore", "carpet muncher", "carpetmuncher", "chocolate rosebuds", "circlejerk", "cleveland steamer", "clit", "clitoris", "clover clamps", "clusterfuck", "cock", "cocks", "coprolagnia", "coprophilia", "cornhole", "coon", "coons", "creampie", "cum", "cumming", "cunnilingus", "cunt", "darkie", "date rape", "daterape", "deep throat", "deepthroat", "dendrophilia", "dick", "dildo", "dingleberry", "dingleberries", "dirty pillows", "dirty sanchez", "doggie style", "doggiestyle", "doggy style", "doggystyle", "dog style", "dolcett", "domination", "dominatrix", "dommes", "donkey punch", "double dong", "double penetration", "dp action", "dry hump", "dvda", "eat my ass", "ecchi", "ejaculation", "erotic", "erotism", "escort", "eunuch", "faggot", "fecal", "felch", "fellatio", "feltch", "female squirting", "femdom", "figging", "fingerbang", "fingering", "fisting", "foot fetish", "footjob", "frotting", "fuck", "fuck buttons", "fucked", "fucker", "fuckers", "fuckface", "fuckhole", "fuckin", "fucking", "fucktards","2 girls 1 cup", "2g1c", "4r5e", "5h1t", "5hit", "a$$", "a$$hole", "a_s_s", "a2m", "a54", "a55", "a55hole", "aeolus", "ahole", "alabama hot pocket", "alaskan pipeline", "anal", "anal impaler", "anal leakage", "analannie", "analprobe", "analsex", "anilingus", "anus", "apeshit", "ar5e", "areola", "areole", "arian", "arrse", "arse", "arsehole", "aryan", "ass", "ass fuck", "ass hole", "assault", "assbag", "assbagger", "assbandit", "assbang", "assbanged", "assbanger", "assbangs", "assbite", "assblaster", "assclown", "asscock", "asscracker", "asses", "assface", "assfaces", "assfuck", "assfucker", "ass-fucker", "assfukka", "assgoblin", "assh0le", "asshat", "ass-hat", "asshead", "assho1e", "asshole", "assholes", "asshopper", "asshore", "ass-jabber", "assjacker", "assjockey", "asskiss", "asskisser", "assklown", "asslick", "asslicker", "asslover", "assman", "assmaster", "assmonkey", "assmucus", "assmunch", "assmuncher", "assnigger", "asspacker", "asspirate", "ass-pirate", "asspuppies", "assranger", "assshit", "assshole", "asssucker", "asswad", "asswhole", "asswhore", "asswipe", "asswipes", "auto erotic", "autoerotic", "axwound", "azazel", "azz", "b!tch", "b00bs", "b17ch", "b1tch", "babe", "babeland", "babes", "baby batter", "baby juice", "badfuck", "ball gag", "ball gravy", "ball kicking", "ball licking", "ball sack", "ball sucking", "ballbag", "balllicker", "balls", "ballsack", "bampot", "bang", "bangyou box", "bangbros", "banger", "banging", "bareback", "barely legal", "barenaked", "barf", "barface", "barfface", "bastard", "bastardo", "bastards", "bastinado", "batty boy", "bawdy", "bazongas", "bazooms", "bbw", "bdsm", "beaner", "beaners", "beardedclam", "beastial", "beastiality", "beatch", "beater", "beatyourmeat", "beaver", "beaver cleaver", "beaver lips", "beef curtain", "beef curtains", "beer", "beeyotch", "bellend", "bender", "beotch", "bestial", "bestiality", "bi+ch", "biatch", "bicurious", "big black", "big breasts", "big knockers", "big tits", "bigbastard", "bigbutt", "bigger", "bigtits", "bimbo", "bimbos", "bint", "birdlock", "bisexual", "bi-sexual", "bitch", "bitch tit", "bitchass", "bitched", "bitcher", "bitchers", "bitches", "bitchez", "bitchin", "bitching", "bitchtits", "bitchy", "black cock", "blonde action", "blonde on blonde action", "bloodclaat", "bloody", "bloody hell", "blow", "blow job", "blow me", "blow mud", "blow your load", "blowjob", "blowjobs", "blue waffle", "blumpkin", "boang", "bod", "bodily", "bogan", "bohunk", "boink", "boiolas", "bollick", "bollock", "bollocks", "bollok", "bollox", "bomd", "bondage", "bone", "boned", "boner", "boners", "bong", "boob", "boobies", "boobs", "booby", "booger", "bookie", "boong", "boonga", "booobs", "boooobs", "booooobs", "booooooobs", "bootee", "bootie", "booty", "booty call", "booze", "boozer", "boozy", "bosom", "bosomy", "bowel", "bowels", "bra", "brassiere", "breast", "breastjob", "breastlover", "breastman", "breasts", "breeder", "brotherfucker", "brown showers", "brunette action", "buceta", "bugger", "buggered", "buggery", "bukkake", "bull shit", "bullcrap", "bulldike", "bulldyke", "bullet vibe", "bullshit", "bullshits", "bullshitted", "bullturds", "bum", "bum boy", "bumblefuck", "bumclat", "bumfuck", "bummer", "bung", "bung hole", "bunga", "bunghole", "bunny fucker", "bust a load", "busty", "butchdike", "butchdyke", "butt", "butt fuck", "butt plug", "buttbang", "butt-bang", "buttcheeks", "buttface", "buttfuck", "butt-fuck", "buttfucka", "buttfucker", "butt-fucker", "butthead", "butthole", "buttman", "buttmuch", "buttmunch", "buttmuncher", "butt-pirate", "buttplug", "c.0.c.k", "c.o.c.k.", "c.u.n.t", "c0ck", "c-0-c-k", "c0cksucker", "caca", "cahone", "camel toe", "cameltoe", "camgirl", "camslut", "camwhore", "carpet muncher", "carpetmuncher", "cawk", "cervix", "chesticle", "chi-chi man", "chick with a dick", "child-fucker", "chin", "chinc", "chincs", "chink", "chinky", "choad", "choade", "choc ice", "chocolate rosebuds", "chode", "chodes", "chota bags", "cipa", "circlejerk", "cl1t", "cleveland steamer", "climax", "clit", "clit licker", "clitface", "clitfuck", "clitoris", "clitorus", "clits", "clitty", "clitty litter", "clogwog", "clover clamps", "clunge", "clusterfuck", "cnut", "cocain", "cocaine", "cock", "c-o-c-k", "cock pocket", "cock snot", "cock sucker", "cockass", "cockbite", "cockblock", "cockburger", "cockeye", "cockface", "cockfucker", "cockhead", "cockholster", "cockjockey", "cockknocker", "cockknoker", "cocklicker", "cocklover", "cocklump", "cockmaster", "cockmongler", "cockmongruel", "cockmonkey", "cockmunch", "cockmuncher", "cocknose", "cocknugget", "cocks", "cockshit", "cocksmith", "cocksmoke", "cocksmoker", "cocksniffer", "cocksucer", "cocksuck", "cocksuck ", "cocksucked", "cocksucker", "cock-sucker", "cocksuckers", "cocksucking", "cocksucks", "cocksuka", "cocksukka", "cockwaffle", "coffin dodger", "coital", "cok", "cokmuncher", "coksucka", "commie", "condom", "coochie", "coochy", "coon", "coonnass", "coons", "cooter", "cop some wood", "coprolagnia", "coprophilia", "corksucker", "cornhole", "corp whore", "cox", "crabs", "crack", "cracker", "crackwhore", "crack-whore", "crap", "crappy", "creampie", "cretin", "crikey", "cripple", "crotte", "cum", "cum chugger", "cum dumpster", "cum freak", "cum guzzler", "cumbubble", "cumdump", "cumdumpster", "cumguzzler", "cumjockey", "cummer", "cummin", "cumming", "cums", "cumshot", "cumshots", "cumslut", "cumstain", "cumtart", "cunilingus", "cunillingus", "cunn", "cunnie", "cunnilingus", "cunntt", "cunny", "cunt", "c-u-n-t", "cunt hair", "cuntass", "cuntbag", "cuntface", "cuntfuck", "cuntfucker", "cunthole", "cunthunter", "cuntlick", "cuntlick ", "cuntlicker", "cuntlicker ", "cuntlicking", "cuntrag", "cunts", "cuntsicle", "cuntslut", "cunt-struck", "cuntsucker", "cut rope", "cyalis", "cyberfuc", "cyberfuck", "cyberfucked", "cyberfucker", "cyberfuckers", "cyberfucking", "cybersex", "d0ng", "d0uch3", "d0uche", "d1ck", "d1ld0", "d1ldo", "dago", "dagos", "dammit", "damn", "damned", "damnit", "darkie", "darn", "date rape", "daterape", "dawgie-style", "deep throat", "deepthroat", "deggo", "dendrophilia", "dick", "dick head", "dick hole", "dick shy", "dickbag", "dickbeaters", "dickbrain", "dickdipper", "dickface", "dickflipper", "dickfuck", "dickfucker", "dickhead", "dickheads", "dickhole", "dickish", "dick-ish", "dickjuice", "dickmilk", "dickmonger", "dickripper", "dicks", "dicksipper", "dickslap", "dick-sneeze", "dicksucker", "dicksucking", "dicktickler", "dickwad", "dickweasel", "dickweed", "dickwhipper", "dickwod", "dickzipper", "diddle", "dike", "dildo", "dildos", "diligaf", "dillweed", "dimwit", "dingle", "dingleberries", "dingleberry", "dink", "dinks", "dipship", "dipshit", "dirsa", "dirty", "dirty pillows", "dirty sanchez", "dlck", "dog style", "dog-fucker", "doggie style", "doggiestyle", "doggie-style", "doggin", "dogging", "doggy style", "doggystyle", "doggy-style", "dolcett", "domination", "dominatrix", "dommes", "dong", "donkey punch", "donkeypunch", "donkeyribber", "doochbag", "doofus", "dookie", "doosh", "dopey", "double dong", "double penetration", "doublelift", "douch3", "douche", "douchebag", "douchebags", "douche-fag", "douchewaffle", "douchey", "dp action", "drunk", "dry hump", "duche", "dumass", "dumb ass", "dumbass", "dumbasses", "dumbcunt", "dumbfuck", "dumbshit", "dummy", "dumshit", "dvda", "dyke", "dykes", "eat a dick", "eat hair pie", "eat my ass", "eatpussy", "ecchi", "ejaculate", "ejaculated", "ejaculates", "ejaculating", "ejaculatings", "ejaculation", "ejakulate", "enlargement", "erect", "erection", "erotic", "erotism", "escort", "essohbee", "eunuch", "extacy", "extasy", "f u c k", "f u c k e r", "f.u.c.k", "f_u_c_k", "f4nny", "facefucker", "facial", "fack", "fag", "fagbag", "fagfucker", "fagg", "fagged", "fagging", "faggit", "faggitt", "faggot", "faggotcock", "faggots", "faggs", "fagot", "fagots", "fags", "fagtard", "faig", "faigt", "fanny", "fannybandit", "fannyflaps", "fannyfucker", "fanyy", "fart", "fartknocker", "fastfuck", "fat", "fatass", "fatfuck", "fatfucker", "fcuk", "fcuker", "fcuking", "fecal", "feck", "fecker", "felch", "felcher", "felching", "fellate", "fellatio", "feltch", "feltcher", "female squirting", "femdom", "fenian", "figging", "fingerbang", "fingerfuck", "fingerfuck ", "fingerfucked", "fingerfucker", "fingerfucker ", "fingerfuckers", "fingerfucking", "fingerfucks", "fingering", "fist fuck", "fisted", "fistfuck", "fistfucked", "fistfucker", "fistfucker ", "fistfuckers", "fistfucking", "fistfuckings", "fistfucks", "fisting", "fisty", "flamer", "flange", "flaps", "fleshflute", "flog the log", "floozy", "foad", "foah", "fondle", "foobar", "fook", "fooker", "foot fetish", "footfuck", "footfucker", "footjob", "footlicker", "foreskin", "freakfuck", "freakyfucker", "freefuck", "freex", "frigg", "frigga", "frotting", "fubar", "fuc", "fuck", "f-u-c-k", "fuck buttons", "fuck hole", "fuck off", "fuck puppet", "fuck trophy", "fuck yo mama", "fuck you", "fucka", "fuckass", "fuck-ass", "fuckbag", "fuck-bitch", "fuckboy", "fuckbrain", "fuckbutt", "fuckbutter", "fucked", "fuckedup", "fucker", "fuckers", "fuckersucker", "fuckface", "fuckfreak", "fuckhead", "fuckheads", "fuckher", "fuckhole", "fuckin", "fucking", "fuckingbitch", "fuckings", "fuckingshitmotherfucker", "fuckme", "fuckme ", "fuckmeat", "fuckmehard", "fuckmonkey", "fucknugget", "fucknut", "fucknutt", "fuckoff", "fucks", "fuckstick", "fucktard", "fuck-tard", "fucktards", "fucktart", "fucktoy", "fucktwat", "fuckup", "fuckwad", "fuckwhit", "fuckwhore", "fuckwit", "fuckwitt", "fuckyou", "fudge packer", "fudgepacker", "fudge-packer", "fuk", "fuker", "fukker", "fukkers", "fukkin", "fuks", "fukwhit", "fukwit", "fuq", "futanari", "fux", "fux0r", "fvck", "fxck", "gae", "gai", "gang bang", "gangbang", "gang-bang", "gangbanged", "gangbangs", "ganja", "gash", "gassy ass", "gay sex", "gayass", "gaybob", "gaydo", "gayfuck", "gayfuckist", "gaylord", "gays", "gaysex", "gaytard", "gaywad", "gender bender", "genitals", "gey", "gfy", "ghay", "ghey", "giant cock", "gigolo", "ginger", "gippo", "girl on", "girl on top", "girls gone wild", "git", "glans", "goatcx", "goatse", "god damn", "godamn", "godamnit", "goddam", "god-dam", "goddammit", "goddamn", "goddamned", "god-damned", "goddamnit", "goddamnmuthafucker", "godsdamn", "gokkun", "golden shower", "goldenshower", "golliwog", "gonad", "gonads", "gonorrehea", "goo girl", "gooch", "goodpoop", "gook", "gooks", "goregasm", "gotohell", "gringo", "grope", "group sex", "gspot", "g-spot", "gtfo", "guido", "guro", "h0m0", "h0mo", "ham flap", "hand job", "handjob", "hard core", "hard on", "hardcore", "hardcoresex", "he11", "headfuck", "hebe", "heeb", "hell", "hemp", "hentai", "heroin", "herp", "herpes", "herpy", "heshe", "he-she", "hitler", "hiv", "ho", "hoar", "hoare", "hobag", "hoe", "hoer", "holy shit", "hom0", "homey", "homo", "homodumbshit", "homoerotic", "homoey", "honkey", "honky", "hooch", "hookah", "hooker", "hoor", "hootch", "hooter", "hooters", "hore", "horniest", "horny", "hot carl", "hot chick", "hotpussy", "hotsex", "how to kill", "how to murdep", "how to murder", "huge fat", "hump", "humped", "humping", "hun", "hussy", "hymen", "iap", "iberian slap", "inbred", "incest", "injun", "intercourse", "j3rk0ff", "jack off", "jackass", "jackasses", "jackhole", "jackoff", "jack-off", "jaggi", "jagoff", "jail bait", "jailbait", "jap", "japs", "jelly donut", "jerk", "jerk off", "jerk0ff", "jerkass", "jerked", "jerkoff", "jerk-off", "jigaboo", "jiggaboo", "jiggerboo", "jism", "jiz", "jizm", "jizz", "jizzed", "jock", "juggs", "jungle bunny", "junglebunny", "junkie", "junky", "kafir", "kawk", "kike", "kikes", "kill", "kinbaku", "kinkster", "kinky", "kkk", "klan", "knob", "knob end", "knobbing", "knobead", "knobed", "knobend", "knobhead", "knobjocky", "knobjokey", "kock", "kondum", "kondums", "kooch", "kooches", "kootch", "kraut", "kum", "kummer", "kumming", "kums", "kunilingus", "kunja", "kunt", "kwif", "kyke", "l3i+ch", "l3itch", "labia", "lameass", "lardass", "leather restraint", "leather straight jacket", "lech", "lemon party", "leper", "lesbian", "lesbians", "lesbo", "lesbos", "lez", "lezbian", "lezbians", "lezbo", "lezbos", "lezza", "lezzie", "lezzies", "lezzy", "lmao", "lmfao", "loin", "loins", "lolita", "looney", "lovemaking", "lube", "lust", "lusting", "lusty", "m0f0", "m0fo", "m45terbate", "ma5terb8", "ma5terbate", "mafugly", "make me come", "male squirting", "mams", "masochist", "massa", "masterb8", "masterbat", "masterbat3", "masterbate", "master-bate", "masterbating", "masterbation", "masterbations", "masturbate", "masturbating", "masturbation", "maxi", "mcfagget", "menage a trois", "menses", "menstruate", "menstruation", "meth", "m-fucking", "mick", "middle finger", "midget", "milf", "minge", "minger", "missionary position", "mof0", "mofo", "mo-fo", "molest", "mong", "moo moo foo foo", "moolie", "moron", "mothafuck", "mothafucka", "mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin", "mothafucking", "mothafuckings", "mothafucks", "mother fucker", "motherfuck", "motherfucka", "motherfucked", "motherfucker", "motherfuckers", "motherfuckin", "motherfucking", "motherfuckings", "motherfuckka", "motherfucks", "mound of venus", "mr hands", "mtherfucker", "mthrfucker", "mthrfucking", "muff", "muff diver", "muff puff", "muffdiver", "muffdiving", "munging", "munter", "murder", "mutha", "muthafecker", "muthafuckaz", "muthafuckker", "muther", "mutherfucker", "mutherfucking", "muthrfucking", "n1gga", "n1gger", "nad", "nads", "naked", "nambla", "napalm", "nappy", "nawashi", "nazi", "nazism", "need the dick", "negro", "neonazi", "nig nog", "nigaboo", "nigg3r", "nigg4h", "nigga", "niggah", "niggas", "niggaz", "nigger", "niggers", "niggle", "niglet", "nig-nog", "nimphomania", "nimrod", "ninny", "nipple", "nipples", "nob", "nob jokey", "nobhead", "nobjocky", "nobjokey", "nonce", "nooky", "nsfw images", "nude", "nudity", "numbnuts", "nut butter", "nut sack", "nutsack", "nutter", "nympho", "nymphomania", "octopussy", "old bag", "omg", "omorashi", "one cup two girls", "one guy one jar", "opiate", "opium", "oral", "orally", "organ", "orgasim", "orgasims", "orgasm", "orgasmic", "orgasms", "orgies", "orgy", "ovary", "ovum", "ovums", "p.u.s.s.y.", "p0rn", "paddy", "paedophile", "paki", "panooch", "pansy", "pantie", "panties", "panty", "pastie", "pasty", "pawn", "pcp", "pecker", "peckerhead", "pedo", "pedobear", "pedophile", "pedophilia", "pedophiliac", "pee", "peepee", "pegging", "penetrate", "penetration", "penial", "penile", "penis", "penisbanger", "penisfucker", "penispuffer", "perversion", "peyote", "phalli", "phallic", "phone sex", "phonesex", "phuck", "phuk", "phuked", "phuking", "phukked", "phukking", "phuks", "phuq", "piece of shit", "pigfucker", "pikey", "pillowbiter", "pimp", "pimpis", "pinko", "piss", "piss off", "piss pig", "pissed", "pissed off", "pisser", "pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff", "piss-off", "pisspig", "playboy", "pleasure chest", "pms", "polack", "pole smoker", "polesmoker", "pollock", "ponyplay", "poof", "poon", "poonani", "poonany", "poontang", "poop", "poop chute", "poopchute", "poopuncher", "porch monkey", "porchmonkey", "porn", "porno", "pornography", "pornos", "pot", "potty", "prick", "pricks", "prickteaser", "prig", "prince albert piercing", "prod", "pron", "prostitute", "prude", "psycho", "pthc", "pube", "pubes", "pubic", "pubis", "punani", "punanny", "punany", "punkass", "punky", "punta", "puss", "pusse", "pussi", "pussies", "pussy", "pussy fart", "pussy palace", "pussylicking", "pussypounder", "pussys", "pust", "puto", "queaf", "queef", "queer", "queerbait", "queerhole", "queero", "queers", "quicky", "quim", "racy", "raghead", "raging boner", "rape", "raped", "raper", "rapey", "raping", "rapist", "raunch", "rectal", "rectum", "rectus", "reefer", "reetard", "reich", "renob", "retard", "retarded", "reverse cowgirl", "revue", "rimjaw", "rimjob", "rimming", "ritard", "rosy palm", "rosy palm and her 5 sisters", "rtard", "r-tard", "rubbish", "rum", "rump", "rumprammer", "ruski", "rusty trombone", "s hit", "s&m", "s.h.i.t.", "s.o.b.", "s_h_i_t", "s0b", "sadism", "sadist", "sambo", "sand nigger", "sandbar", "sandler", "sandnigger", "sanger", "santorum", "sausage queen", "scag", "scantily", "scat", "schizo", "schlong", "scissoring", "screw", "screwed", "screwing", "scroat", "scrog", "scrot", "scrote", "scrotum", "scrud", "scum", "seaman", "seamen", "seduce", "seks", "semen", "sex", "sexo", "sexual", "sexy", "sh!+", "sh!t", "sh1t", "s-h-1-t", "shag", "shagger", "shaggin", "shagging", "shamedame", "shaved beaver", "shaved pussy", "shemale", "shi+", "shibari", "shirt lifter", "shit", "s-h-i-t", "shit ass", "shit fucker", "shitass", "shitbag", "shitbagger", "shitblimp", "shitbrains", "shitbreath", "shitcanned", "shitcunt", "shitdick", "shite", "shiteater", "shited", "shitey", "shitface", "shitfaced", "shitfuck", "shitfull", "shithead", "shitheads", "shithole", "shithouse", "shiting", "shitings", "shits", "shitspitter", "shitstain", "shitt", "shitted", "shitter", "shitters", "shittier", "shittiest", "shitting", "shittings", "shitty", "shiz", "shiznit", "shota", "shrimping", "sissy", "skag", "skank", "skeet", "skullfuck", "slag", "slanteye", "slave", "sleaze", "sleazy", "slope", "slut", "slut bucket", "slutbag", "slutdumper", "slutkiss", "sluts", "smartass", "smartasses", "smeg", "smegma", "smut", "smutty", "snatch", "sniper", "snowballing", "snuff", "s-o-b", "sod off", "sodom", "sodomize", "sodomy", "son of a bitch", "son of a motherless goat", "son of a whore", "son-of-a-bitch", "souse", "soused", "spac", "spade", "sperm", "spic", "spick", "spik", "spiks", "splooge", "splooge moose", "spooge", "spook", "spread legs", "spunk", "steamy", "stfu", "stiffy", "stoned", "strap on", "strapon", "strappado", "strip", "strip club", "stroke", "stupid", "style doggy", "suck", "suckass", "sucked", "sucking", "sucks", "suicide girls", "sultry women", "sumofabiatch", "swastika", "swinger", "t1t", "t1tt1e5", "t1tties", "taff", "taig", "tainted love", "taking the piss", "tampon", "tard", "tart", "taste my", "tawdry", "tea bagging", "teabagging", "teat", "teets", "teez", "terd", "teste", "testee", "testes", "testical", "testicle", "testis", "threesome", "throating", "thrust", "thug", "thundercunt", "tied up", "tight white", "tinkle", "tit", "tit wank", "titfuck", "titi", "tities", "tits", "titt", "tittie5", "tittiefucker", "titties", "titty", "tittyfuck", "tittyfucker", "tittywank", "titwank", "toke", "tongue in a", "toots", "topless", "tosser", "towelhead", "tramp", "tranny", "transsexual", "trashy", "tribadism", "trumped", "tub girl", "tubgirl", "turd", "tush", "tushy", "tw4t", "twat", "twathead", "twatlips", "twats", "twatty", "twatwaffle", "twink", "twinkie", "two fingers", "two fingers with tongue", "two girls one cup", "twunt", "twunter", "ugly", "unclefucker", "undies", "undressing", "unwed", "upskirt", "urethra play", "urinal", "urine", "urophilia", "uterus", "uzi", "v14gra", "v1gra", "vag", "vagina", "vajayjay", "va-j-j", "valium", "venus mound", "veqtable", "viagra", "vibrator", "violet wand", "virgin", "vixen", "vjayjay", "vodka", "vomit", "vorarephilia", "voyeur", "vulgar", "vulva", "w00se", "wad", "wang", "wank", "wanker", "wankjob", "wanky", "wazoo", "wedgie", "weed", "weenie", "weewee", "weiner", "weirdo", "wench", "wet dream", "wetback", "wh0re", "wh0reface", "white power", "whitey", "whiz", "whoar", "whoralicious", "whore", "whorealicious", "whorebag", "whored", "whoreface", "whorehopper", "whorehouse", "whores", "whoring", "wigger", "willies", "willy", "window licker", "wiseass", "wiseasses", "wog", "womb", "woody", "wop", "wrapping men", "wrinkled starfish", "wtf", "xrated", "x-rated", "xx", "xxx", "yaoi", "yeasty", "yellow showers", "yid", "yiffy", "yobbo", "zoophile", "zoophilia", "zubb"};

        //text to be checked
        String text = edtMessageInput.getText().toString();

        // Check for spam words

        for (String word : badWords) {
            if (Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE).matcher(text).find()) {

                isBad = true;
                break;


            }
        }

        if(isBad){
            //Toast.makeText(MainActivity.this, "This Is Violent Content", Toast.LENGTH_SHORT).show();
//            dialog.show();
            return true;
        }else{
//            Toast.makeText(MainActivity.this, "This is Normal Text", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    //method for chat rooms
    private void setUpChatRoom(){

        FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String myUsername = snapshot.getValue(User.class).getUsername();

                //comparing usernames alphabetically

                if(usernameOfTheRoommate.compareTo(myUsername) > 0){

                    //for xyz comapring with abc alphabitacally

                    chatRoomId = myUsername + usernameOfTheRoommate;


                } else if (usernameOfTheRoommate.compareTo(myUsername) == 0) {

                    //for abc comparing with abc
                    chatRoomId = myUsername + usernameOfTheRoommate;
                }else {
                    chatRoomId = usernameOfTheRoommate + myUsername;
                }

                attachMessageListener(chatRoomId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("messages/"+ chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //AES Encryption method

    private String AESencryption(String Encryptedstr){

        try {
            SecretKeySpec keySpec = new SecretKeySpec(EncryptionKEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedValue = cipher.doFinal(Encryptedstr.getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(encryptedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Encryptedstr;

    }





}
