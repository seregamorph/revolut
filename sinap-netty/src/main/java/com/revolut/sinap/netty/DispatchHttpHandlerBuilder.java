package com.revolut.sinap.netty;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Simple dispatching http handler builder.
 * Dispatcher chooses first matching uri.
 * <p/>
 * todo example of code to use.
 * <p/>
 * todo wildcard patterns instead of regex Pattern
 */
public class DispatchHttpHandlerBuilder {
    private final List<HttpHandlerBinding> bindings = new ArrayList<>();

    public DispatchHttpHandlerBuilder bind(Pattern uriPattern, HttpHandler handler) {
        HttpHandlerBinding binding = new HttpHandlerBinding(uriPattern, handler);
        bindings.add(binding);
        return this;
    }

    /**
     * Bind uri to handler. Shortcut for
     * <pre>
     * bind(Pattern.compile(Pattern.quote(uri)), handler)
     * </pre>
     *
     * @param uri
     * @param handler
     * @return
     */
    public DispatchHttpHandlerBuilder bind(String uri, HttpHandler handler) {
        Pattern uriPattern = Pattern.compile(Pattern.quote(uri));
        return bind(uriPattern, handler);
    }

    public HttpHandler build() {
        List<HttpHandlerBinding> immutableBindings = new ArrayList<>(this.bindings);
        return new AbstractDispatchHttpHandler() {
            @Nullable
            @Override
            protected HttpHandler getHandler(String requestUri) {
                return immutableBindings.stream()
                        .filter(binding -> binding.matches(requestUri))
                        .map(HttpHandlerBinding::handler)
                        .findFirst()
                        .orElse(null);
            }
        };
    }

    private static class HttpHandlerBinding {
        private final Pattern uriPattern;
        private final HttpHandler handler;

        private HttpHandlerBinding(Pattern uriPattern, HttpHandler handler) {
            this.uriPattern = Objects.requireNonNull(uriPattern, "uriPattern");
            this.handler = Objects.requireNonNull(handler, "handler");
        }

        boolean matches(String uri) {
            return uriPattern.matcher(uri).matches();
        }

        HttpHandler handler() {
            return handler;
        }
    }
}
