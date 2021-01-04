package com.moon.processor.model;

/**
 * @author benshaoye
 */
public enum ConvertType {
    /**
     * 转换类型
     */
    FORWARD {
        @Override
        public boolean isIgnored(DeclareMapping mapping) {
            return mapping.isIgnoreForward();
        }

        @Override
        public DeclareMapping getMappingFor(DeclareProperty self, String classname) {
            return self.getForwardMapping(classname);
        }
    },
    BACKWARD {
        @Override
        public boolean isIgnored(DeclareMapping mapping) {
            return mapping.isIgnoreBackward();
        }

        @Override
        public DeclareMapping getMappingFor(DeclareProperty self, String classname) {
            return self.getBackwardMapping(classname);
        }
    };

    public abstract boolean isIgnored(DeclareMapping mapping);

    public abstract DeclareMapping getMappingFor(DeclareProperty self, String classname);
}
