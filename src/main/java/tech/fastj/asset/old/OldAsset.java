package tech.fastj.asset.old;

public abstract class OldAsset<RawAsset> {

    protected String assetPath;
    protected String assetPathAlias;
    protected RawAsset rawAsset;

    protected OldAsset(String assetPath, String assetPathAlias) {
        this.assetPath = assetPath;
        this.assetPathAlias = assetPathAlias;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getAssetPathAlias() {
        return assetPathAlias;
    }

    public RawAsset getRawAsset() {
        return rawAsset;
    }

    public void setRawAsset(RawAsset rawAsset) {
        this.rawAsset = rawAsset;
        // TODO: fire asset change event
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
        // TODO: fire asset change event
    }

    public void setAssetPathAlias(String assetPathAlias) {
        this.assetPathAlias = assetPathAlias;
        // TODO: fire asset change event
    }

    public <A extends OldAsset<RawAsset>, AI extends OldAssetInfo<RawAsset, A>> void setAssetInfo(AI assetInfo) {
        this.rawAsset = assetInfo.rawAsset();
        this.assetPathAlias = assetInfo.pathAlias();

        // TODO: fire asset change event
    }
}
