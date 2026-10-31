package tests.flow;

import org.junit.jupiter.api.BeforeEach;
import tests.BaseTest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// Các phase của User Journey nối với nhau qua file session: phase N lưu session, phase N+1 mở browser mới từ file đó.
// Vì phase sau cần file của phase trước nên các class này chỉ chạy đúng trong suite tuần tự (@FlowSuite)
public abstract class FlowBaseTest extends BaseTest {
    private static final Path SESSION_ROOT = Paths.get("target", "flow-sessions");
    // Mỗi lần chạy (1 JVM) ghi session vào thư mục riêng -> phase sau không bao giờ đọc nhầm file của lần chạy trước,
    // kể cả khi các phase bị chạy sai thứ tự (vd ép chạy song song, phase 5 bắt đầu trước khi phase 1 kịp dọn file cũ)
    private static final Path SESSION_DIR = SESSION_ROOT.resolve(
            "run-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
    // Lý do phase hiện tại bị chặn (null = chạy bình thường). ThreadLocal vì @BeforeAll và các step của 1 class chạy chung thread
    private static final ThreadLocal<String> blockedReason = new ThreadLocal<>();

    // Dọn session của các lần chạy trước đúng 1 lần, khi class được nạp (JVM đảm bảo chỉ 1 thread chạy khối static),
    // tức là trước khi bất kỳ phase nào bắt đầu
    static {
        deleteDirectory(SESSION_ROOT);
    }

    protected static Path sessionFile(int phase) {
        return SESSION_DIR.resolve("phase-" + phase + ".json");
    }

    // Phase 1 bắt đầu hành trình mới: bỏ trạng thái Blocked có thể còn sót trên thread này từ class chạy trước
    protected static void startJourney() {
        blockedReason.remove();
    }

    private static void deleteDirectory(Path dir) {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Phase trước không để lại session (fail giữa chừng hoặc chưa chạy) -> mọi step của phase này bị skip (Blocked) thay vì fail lan.
    // Không assume ngay trong @BeforeAll: abort ở đó thì JUnit bỏ cả class, step không xuất hiện trong report
    protected static void continueFromPhase(int phase) {
        final Path previousSession = sessionFile(phase);
        if (!Files.exists(previousSession)) {
            blockedReason.set("Phase " + phase + " did not save its session, this phase is blocked");
            return;
        }
        blockedReason.remove();
        restoreSession(previousSession);
    }

    @BeforeEach
    void skipWhenBlocked() {
        assumeTrue(blockedReason.get() == null, blockedReason.get());
    }

    // Trả về nội dung file session vừa lưu để step kiểm tra session có đúng dữ liệu cần chuyển sang phase sau
    protected static String saveSessionForNextPhase(int phase) {
        try {
            Files.createDirectories(SESSION_DIR);
            saveSession(sessionFile(phase));
            return Files.readString(sessionFile(phase));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void verifyCurrentUrl(String expectedUrl) {
        final String actualUrl = browserManager().getCurrentTabUrl(expectedUrl);
        assertEquals(
                expectedUrl,
                actualUrl,
                "The app did not navigate to the expected page. Expected: ["
                        + expectedUrl
                        + "], Actual: ["
                        + actualUrl
                        + "]"
        );
    }
}
