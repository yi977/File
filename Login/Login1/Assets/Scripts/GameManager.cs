using UnityEngine;
using UnityEngine.UI;

public class GameManager : MonoBehaviour
{
    public Text callbackText;
    public Button LoginBtn;
    // Start is called before the first frame update
    void Start()
    {
        LoginBtn.onClick.AddListener(LoginClick);
    }
    public void LoginClick()
    {
        AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity");

        jo.Call("LoginQQ");
    }
    public void AndroidCallBack(string callbackinfo)
    {
        callbackText.text = callbackinfo;
    }
}