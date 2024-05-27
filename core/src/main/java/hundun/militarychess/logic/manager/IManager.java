package hundun.militarychess.logic.manager;

import hundun.militarychess.logic.StageConfig;

public interface IManager {
    void commitFightResult();
    void updateAfterFightOrStart();
    void prepareDone(StageConfig stageConfig);
}
