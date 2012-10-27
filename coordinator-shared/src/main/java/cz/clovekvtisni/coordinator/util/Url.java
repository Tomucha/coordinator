package cz.clovekvtisni.coordinator.util;

import cz.clovekvtisni.coordinator.exception.MaException;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 12/9/11
 * Time: 10:35 PM
 */
public class Url implements Serializable {

    private static final long serialVersionUID = -2783232981314811818L;

    private String protocol;
    
    private String host;
    
    private TreeMap<String, ArrayList<String>> params = new TreeMap<String, ArrayList<String>>();
    
    private String anchor;
    
    private List<String> path = new ArrayList<String>();

    private static final String CHARS_FOR_SHORTENER_DEF = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";

    private static final char[] CHARS_FOR_SHORTENER = new char[CHARS_FOR_SHORTENER_DEF.length()];
    private static final int[] CHARS_FOR_SHORTENER_REVERSE_MAP;
    private static final int CHARS_FOR_SHORTENER_OFFSET;

    static {
        int minCharValue = 255;
        int maxCharValue = 0;
        for (int i = CHARS_FOR_SHORTENER.length - 1; i >= 0; i--) {
            char c = CHARS_FOR_SHORTENER_DEF.charAt(i);
            CHARS_FOR_SHORTENER[i] = c;
            int v = (int)c;
            minCharValue = Math.min(minCharValue, v);
            maxCharValue = Math.max(maxCharValue, v);
        }
        CHARS_FOR_SHORTENER_OFFSET = minCharValue;
        CHARS_FOR_SHORTENER_REVERSE_MAP = new int[maxCharValue - minCharValue + 1];
        int v = 0;
        for (char c : CHARS_FOR_SHORTENER) {
            CHARS_FOR_SHORTENER_REVERSE_MAP[(int)c - minCharValue] = v++;
        }
    }

    public static Url valueOf(String str) {
        if (ValueTool.isEmpty(str)) {
            return null;
        }

        return new Url(str);
    }

    public Url() {
    }

    public Url(Url url) {
        protocol = url.protocol;
        host = url.host;
        params = new TreeMap<String, ArrayList<String>>();
        for (Map.Entry<String, ArrayList<String>> entry : url.params.entrySet()) {
            params.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
        }
        anchor = url.anchor;
        path = new ArrayList<String>(url.path);
    }

    public Url(String url) {
        int protocolIdx = url.indexOf("://");
        int pathStartIdx = 0;
        int paramsSepIdx = url.indexOf('?');
        int anchorSepIdx = url.indexOf('#', Math.max(0, paramsSepIdx));
        int pathEndIdx = paramsSepIdx > 0 ? paramsSepIdx : (anchorSepIdx > 0 ? anchorSepIdx : url.length());
        if (protocolIdx > 0) {
            protocol = url.substring(0, protocolIdx);
            protocolIdx += 2;
            pathStartIdx = url.indexOf("/", protocolIdx + 1);
            if (pathStartIdx > pathEndIdx) {
                pathStartIdx = -1;
            }
            if (pathStartIdx > 0) {
                host = url.substring(protocolIdx + 1, pathStartIdx);
            }
            else {
                host = url.substring(protocolIdx + 1, pathEndIdx);
            }
        }
        if (pathStartIdx >= 0) {
            String pathStr = decode(pathEndIdx > 0 ? url.substring(pathStartIdx, pathEndIdx) : url.substring(pathStartIdx));
            int pos = pathStr.indexOf('/');
            int prevPos = 0;
            while (pos >= 0) {
                if (pos > 0) {
                    path.add(pathStr.substring(prevPos, pos));
                }
                prevPos = pos + 1;
                pos = pathStr.indexOf('/', prevPos);
            }
            if (prevPos == pathStr.length()) {
                path.add("");
            }
            else {
                path.add(pathStr.substring(prevPos));
            }
        }
        if (anchorSepIdx > 0) {
            anchor = anchorSepIdx < url.length() ? decode(url.substring(anchorSepIdx + 1)) : "";
        }
        if (paramsSepIdx > 0) {
            String queryString;
            paramsSepIdx++;
            if (anchorSepIdx > 0) {
                queryString = url.substring(paramsSepIdx, anchorSepIdx);
            }
            else {
                queryString = paramsSepIdx < url.length() ? url.substring(paramsSepIdx) : "";
            }
            parseParams(queryString);
        }
    }

    private void parseParams(String queryString) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] param = pair.split("=", 2);
            add(decode(param[0]), param.length == 1 ? "" : decode(param[1]));
        }
    }

    public Url set(String param, String value) {
        remove(param);
        add(param, value);
        return this;
    }

    public Url add(String param, String value) {
        ArrayList<String> values = params.get(param);
        if (values == null) {
            values = new ArrayList<String>();
            params.put(param, values);
        }
        values.add(value);
        return this;
    }
    
    public Url remove(String param) {
        params.remove(param);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (protocol != null) {
            sb.append(protocol).append("://").append(host);
        }
        if (path.isEmpty()) {
            sb.append('/');
        }
        else {
            for (String segment : path) {
                sb.append('/').append(segment);
            }
        }
        if (!params.isEmpty()) {
            sb.append("?");
            int idx = 0;
            for (Map.Entry<String, ArrayList<String>> entry : params.entrySet()) {
                String param = encode(entry.getKey());
                if (entry.getValue() != null) {
                    for (String value : entry.getValue()) {
                        if (idx++ > 0) {
                            sb.append("&");
                        }
                        sb.append(param).append("=");
                        if (value != null) {
                            sb.append(encode(value));
                        }
                    }
                }
            }
        }
        if (anchor != null) {
            sb.append("#").append(encode(anchor));
        }
        return sb.toString();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public Map<String, ArrayList<String>> getParams() {
        return params;
    }

    public String getAnchor() {
        return anchor;
    }

    public String[] getPath() {
        return path.toArray(new String[path.size()]);
    }

    public Url setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public Url setHost(String host) {
        this.host = host;
        return this;
    }

    public Url setParams(Map<String, ArrayList<String>> params) {
        this.params.clear();
        this.params.putAll(params);
        return this;
    }

    public Url setAnchor(String anchor) {
        this.anchor = anchor;
        return this;
    }

    public Url setPath(String... path) {
        this.path.clear();
        this.path.addAll(Arrays.asList(path));
        return this;
    }

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String encode(String string) {
   		try {
   			return java.net.URLEncoder.encode(string, "utf8");
   		} catch (Exception e) {
   			throw MaException.internal(e.toString());
   		}
   	}

   	@SuppressWarnings("NonJREEmulationClassesInClientCode")
       public static String decode(String string) {
   		try {
   			return java.net.URLDecoder.decode(string, "utf8");
   		} catch (Exception e) {
               throw MaException.internal(e.toString());
   		}
   	}

    public void prependPathSegment(String pathSegment) {
        path.add(0, encode(pathSegment));
    }

    public void setPathSegment(int index, String pathSegment) {
        path.set(index, pathSegment);
    }

    public void addPathSegment(String pathSegment) {
        int lastIndex = path.size() - 1;
        if (lastIndex >= 0 && ValueTool.isEmpty(path.get(lastIndex))) {
            path.set(lastIndex, pathSegment);
        }
        else {
            path.add(encode(pathSegment));
        }
    }

    public String shortenValue(long value) {
        int base = CHARS_FOR_SHORTENER.length;
        StringBuilder sb = new StringBuilder();
        while (value >= base) {
            sb.append(CHARS_FOR_SHORTENER[((int) (value % base))]);
            value /= base;
        }
        sb.append(CHARS_FOR_SHORTENER[((int) (value % base))]);
        return sb.toString();
    }

    public long shortenedValueAsLong(String shortened) {
        int base = CHARS_FOR_SHORTENER.length;
        long value = 0;
        for (int i = shortened.length() - 1; i >= 0; i--) {
            int offset = (int)shortened.charAt(i) - CHARS_FOR_SHORTENER_OFFSET;
            value = value * base + CHARS_FOR_SHORTENER_REVERSE_MAP[offset];
        }
        return value;
    }
}
