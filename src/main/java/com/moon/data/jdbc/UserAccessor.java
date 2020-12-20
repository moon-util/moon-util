package com.moon.data.jdbc;

import com.moon.core.lang.StringUtil;
import com.moon.data.jdbc.annotation.*;

import javax.persistence.Access;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
public interface UserAccessor {

    UserLoginInfo findByUsernameLikeAndUserType(@IfNotEmpty String username, @IfNotBlank String userType);

    List<String> findUsernameByUsernameLike(String username);

    class UserAccessorImpl implements UserAccessor {

        @Override
        public List<String> findUsernameByUsernameLike(String username) {
            return null;
        }

        @Override
        public UserLoginInfo findByUsernameLikeAndUserType(@IfNotEmpty String username, @IfNotBlank String userType) {
            StringBuilder builder = new StringBuilder("SELECT id, username, password FROM t_user");
            List<Object> params = new ArrayList<>();
            List<String> wheres = new ArrayList<>();

            // default
            if (username == null) {
                wheres.add(" username IS NULL ");
            } else {
                wheres.add(" username LIKE ? ");
                params.add(username);
            }

            // @IfNotNull
            if (username != null) {
                wheres.add(" username = ? ");
                params.add(username);
            }

            // @IfNotEmpty
            if (StringUtil.isNotEmpty(username)) {
                wheres.add(" username = ? ");
                params.add(username);
            }

            // @IfNotBlank
            if (StringUtil.isNotBlank(username)) {
                wheres.add(" username = ? ");
                params.add(username);
            }

            int idx = 0;
            for (String where : wheres) {
                if (idx++ == 0) {
                    builder.append(where);
                } else {
                    builder.append(" AND ").append(where);
                }
            }
            return null;
        }
    }
}
