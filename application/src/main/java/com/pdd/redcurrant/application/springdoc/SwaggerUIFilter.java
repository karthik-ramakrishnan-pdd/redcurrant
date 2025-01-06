package com.pdd.redcurrant.application.springdoc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Filter to dynamically inject custom JavaScript into Swagger UI.
 */
@WebFilter("/swagger-ui/index.html")
@RequiredArgsConstructor
public class SwaggerUIFilter extends OncePerRequestFilter {

    private final String scriptPath;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!"/swagger-ui/index.html".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        HtmlResponseWrapper responseWrapper = new HtmlResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        String content = responseWrapper.getCaptureAsString();
        content = content.replace("</body>",
                String.format("<script src=\"%s\" charset=\"UTF-8\"></script></body>", scriptPath));

        response.setContentType("text/html;charset=UTF-8");
        response.setContentLength(content.length());
        response.getWriter().write(content);
    }

    public static class HtmlResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream capture;

        private ServletOutputStream output;

        private PrintWriter writer;

        public HtmlResponseWrapper(HttpServletResponse response) {
            super(response);
            capture = new ByteArrayOutputStream(response.getBufferSize());
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }

            if (output == null) {
                output = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        capture.write(b);
                    }

                    @Override
                    public void flush() throws IOException {
                        capture.flush();
                    }

                    @Override
                    public void close() throws IOException {
                        capture.close();
                    }

                    @Override
                    public boolean isReady() {
                        return false;
                    }

                    @Override
                    public void setWriteListener(WriteListener arg0) {
                    }
                };
            }

            return output;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (output != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }

            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(capture, getCharacterEncoding()));
            }

            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            super.flushBuffer();

            if (writer != null) {
                writer.flush();
            }
            else if (output != null) {
                output.flush();
            }
        }

        public byte[] getCaptureAsBytes() throws IOException {
            if (writer != null) {
                writer.close();
            }
            else if (output != null) {
                output.close();
            }

            return capture.toByteArray();
        }

        public String getCaptureAsString() throws IOException {
            return new String(getCaptureAsBytes(), getCharacterEncoding());
        }

    }

}
