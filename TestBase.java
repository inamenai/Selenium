import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Wait;

/**
 * Seleniumテスト実行の基底となる抽象クラス。
 * @author inamenai
 */
public abstract class TestBase extends SeleneseTestBase {

    // ----------ここから設定値----------
    /** テストサイトのURL */
    private final String URL = "http://example.com/";
    /** 使用するブラウザ */
    private final Browser BROWSER = Browser.CHROME;
    /** ウィンドウサイズ（高さ） */
    private final int WINDOW_WIDTH = 1000;
    /** ウィンドウサイズ（幅） */
    private final int WINDOW_HEIGHT = 800;
    /** ページ読み込みやAjax処理の最大待ち時間（ミリ秒） */
    protected final Integer TIMEOUT = 15 * 1000;
    // ----------ここまで設定値----------

    public enum Browser {
        IE {
            public String getName() { return "*iexplore"; }
            public boolean uploadFile() { return false; }
        },
        FIREFOX {
            public String getName() { return "*firefox"; }
            public boolean uploadFile() { return true; }
        },
        CHROME {
            public String getName() { return "*googlechrome"; }
            public boolean uploadFile() { return false; }
        };
        protected abstract String getName();
        protected abstract boolean uploadFile();
    }
    private static SeleniumServer seleniumServer;

    public TestBase() { super(); }


// JUnit関連
    @BeforeClass
    public static void init() throws Exception {
        seleniumServer = new SeleniumServer();
        seleniumServer.start();
    }

    @Before
    public void before() throws Exception {
        setUp(URL, BROWSER.getName());
        selenium.runScript("window.resizeTo(" + WINDOW_WIDTH + ", " + WINDOW_HEIGHT + ");");
//        selenium.windowMaximize();
    }

    @After
    public void after() throws Exception {
        tearDown();
    }

    @AfterClass
    public static void finish() {
        seleniumServer.stop();
    }


// ユーティリティ
    /**
     * isElementPresent()が真になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForElementPresent(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return selenium.isElementPresent(locator); }
        };
        wait.wait("Failed to waitForElementPresent() at " + locator + ".", TIMEOUT);
    }

    /**
     * isElementPresent()が偽になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForElementNotPresent(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return !selenium.isElementPresent(locator); }
        };
        wait.wait("Failed to waitForElementNotPresent() at " + locator + ".", TIMEOUT);
    }

    /**
     * isVisible()が真になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForVisible(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return selenium.isVisible(locator); }
        };
        wait.wait("Failed to waitForVisible() at " + locator + ".", TIMEOUT);
    }

    /**
     * isVisible()が偽になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForNotVisible(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return !selenium.isVisible(locator); }
        };
        wait.wait("Failed to waitForNotVisible() at " + locator + ".", TIMEOUT);
    }

    /**
     * isEditable()が真になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForEditable(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return selenium.isEditable(locator); }
        };
        wait.wait("Failed to waitForEditable() at " + locator + ".", TIMEOUT);
    }

    /**
     * isEditable()が偽になるまでwaitする。
     * @param locator lcoator
     */
    protected void waitForNotEditable(final String locator) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return !selenium.isEditable(locator); }
        };
        wait.wait("Failed to waitForNotEditable() at " + locator + ".", TIMEOUT);
    }

    /**
     * 要素の属性が期待値と等しくなるまでwaitする。
     * @param attributeLocator attributeLocator
     * @param expected 期待値
     */
    protected void waitForAttributeEquals(final String attributeLocator, final String expected) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return selenium.getAttribute(attributeLocator).equals(expected); }
        };
        wait.wait("Failed to waitForAttributeEquals() at " + attributeLocator + " equals " + expected + ".", TIMEOUT);
    }

    /**
     * 要素の属性が期待値と等しくなくなるまでwaitする。
     * @param attributeLocator attributeLocator
     * @param expected 期待値
     */
    protected void waitForAttributeNotEquals(final String attributeLocator, final String expected) {
        Wait wait = new Wait() {
            @Override
            public boolean until() { return !selenium.getAttribute(attributeLocator).equals(expected); }
        };
        wait.wait("Failed to waitForAttributeNotEquals() at " + attributeLocator + " equals " + expected + ".", TIMEOUT);
    }

    /**
     * 現在Seleniumで選択中のwindowの名称をJSから取得する。
     * @return 現在のwindow名
     */
    protected String getCurrentWindowName() {
        return selenium.getEval("selenium.browserbot.getCurrentWindow().name");
    }

    /**
     * openedウィンドウのopenerの値にopenerのウィンドウを設定する。
     * @param opener オープン元のウィンドウの名前
     * @param opened オープンされたウィンドウの名前
     */
    protected void setOpener(String opener, String opened) {
        selenium.getEval("var opener = selenium.browserbot.getWindowByName('" + opener + "'); " +
                         "var opened = selenium.browserbot.getWindowByName('" + opened + "');" +
                         "opened.opener = opener;"
                        );
    }

    /**
     * ファイルアップロードが可能なブラウザかどうかを判断する。
     * @return boolean
     */
    protected boolean isUploadable() {
        return BROWSER.uploadFile();
    }

// サイト内の汎用処理
    // ログイン、メニュー画面へ遷移、テストデータ作成など
}
