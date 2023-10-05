import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.Encoder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import io.vertx.core.Launcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

public class BenchmarkServer extends AbstractVerticle {
    static {
        Brotli4jLoader.ensureAvailability();
    }
    private final byte[] RESPONSE_BYTES = ClassLoader.getSystemResourceAsStream("amazon.html").readAllBytes();

    final Encoder.Parameters parameters = new Encoder.Parameters().setQuality(1);

    public BenchmarkServer() throws IOException {
    }

    public static void main(String[] args) {
        Launcher.executeCommand("run", BenchmarkServer.class.getName());
    }

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);
        Route compressionRoute = router.route("/compression");


        compressionRoute.handler(routingContext -> {
            try {
                HttpServerRequest request = routingContext.request();
                String algorithm = Optional.ofNullable(request.params().get("algorithm")).orElse("plain");
                if (algorithm.equals("brotli")) {
                    serveBrotliCompressedHtml(routingContext.request());
                } else if (algorithm.equals("gzip")) {
                    serveGzipCompressedHtml(routingContext.request());
                } else {
                    servePlainHtml(routingContext.request());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private void servePlainHtml(HttpServerRequest request) throws IOException {
        HttpServerResponse response = request.response();

        InputStream responseStream = new ByteArrayInputStream(RESPONSE_BYTES);

        response.setChunked(true);
        response.setStatusCode(200);
        response.putHeader("Content-Type", "text/html");
        response.putHeader("Transfer-Encoding", "chunked");
        byte[] buffer = new byte[4096];

        while ( responseStream.read(buffer) != -1) {
            response.write(Buffer.buffer(buffer));
        }
        response.end();
    }

    private void serveBrotliCompressedHtml(HttpServerRequest request) throws IOException {
        HttpServerResponse response = request.response();

        InputStream responseStream = new ByteArrayInputStream(RESPONSE_BYTES);

        response.setChunked(true);
        response.setStatusCode(200);
        response.putHeader("Content-Type", "text/html");
        response.putHeader("Transfer-Encoding", "chunked");
        response.putHeader("Content-Encoding", "br");
        byte[] buffer = new byte[100_000];

        while (responseStream.read(buffer) != -1) {
            response.write(Buffer.buffer(com.aayushatharva.brotli4j.encoder.Encoder.compress(buffer, parameters)));
        }
        response.end();
    }

    private void serveGzipCompressedHtml(HttpServerRequest request) throws IOException {
        HttpServerResponse response = request.response();

        InputStream responseStream = new ByteArrayInputStream(RESPONSE_BYTES);

        response.setChunked(true);
        response.setStatusCode(200);
        response.putHeader("Content-Type", "text/html");
        response.putHeader("Transfer-Encoding", "chunked");
        response.putHeader("Content-Encoding", "gzip");

        byte[] buffer = new byte[100_000];

        while (responseStream.read(buffer) != -1) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(buffer);
            gzipOutputStream.close();
            response.write(Buffer.buffer(outputStream.toByteArray()));
        }
        response.end();
    }

}
