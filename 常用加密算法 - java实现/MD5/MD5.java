public static String getMD5(String source) {
        if (source == null || source.length() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(32);
        try {
            MessageDigest md 	= MessageDigest.getInstance("MD5");
            byte[] array 		= md.digest(source.getBytes("utf-8"));

            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1, 3));
            }
        } catch (Exception e) {
            Log.e("ÎÞ·¨×ª»»" + source + "' to MD5!", e.toString());
            return null;
        }
        return sb.toString();
}