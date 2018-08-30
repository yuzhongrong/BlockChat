package com.ipeercloud.com.controler;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: jni调用管理类
 */

public class GsSocketManager {

    private static GsSocketManager instance;
    private static final String TAG = "GsSocketManager";

    private GsSocketManager() {

    }

    public static GsSocketManager getInstance() {
        if (instance == null) {
            instance = new GsSocketManager();
        }
        return instance;
    }



    /**
     * 下载文件时jni对java的回调
     *
     * @param finishLength 已经完成的长度
     * @param totalLength  总共的长度
     * @param id           文件的id
     *                     0表示正常下载；-1表示暂停
     */
    public static int gsGetFileCallbackId(long finishLength, long totalLength, String id) {
        if (mJniCb != null) {
            return mJniCb.onJniGetFileCallback(finishLength, totalLength, id);
        }
        return 0;
    }

    /**
     * 上传文件时jni对java的回调
     *
     * @param finishLength 已经完成的长度
     * @param totalLength  总共的长度
     * @param id           文件的id
     *                     0表示正常上传；-1表示暂停
     */
    public static int gsPutFileCallbackId(long totalLength, long finishLength, String id) {
        if (mJniCb != null) {
            return mJniCb.onJniPutFileCallback(totalLength, finishLength, id);
        }
        return 0;
    }

    public interface GsJniCallback {
        int onJniPutFileCallback(long totalLength, long finishLength, String id);

        int onJniGetFileCallback(long finishLength, long totalLength, String id);
    }

    private static GsJniCallback mJniCb;

    public void setJniCb(GsJniCallback jniCb) {
        this.mJniCb = jniCb;
    }


    //----------new api---------
    public native boolean gsGproxyInit(String serverIp, int port, String userName, String pwd);

    public native int gsConnectServer();

    public synchronized native int gsConnectPeerEx();
    public synchronized native int gsGQueryUser(String server,int port, String user, String password);
    public synchronized native String gsGetDevice2(String ip,int port,String user,String password);
    public native int gsTransferUserID(String ip,int port,String newUser,String newPassword,String oldUser,String oldPassword);
    public native boolean gsModifyDeviceName(String ip,int port,String phoneOrEmail,String password,long deviceId,String deviceName);
    public synchronized native int gsConnectPeerEx(long deviceId);
    //链接网盘
    public synchronized native int gsConnectPeer();

    //定义不同的gpfIndex各不同的模块分开使用，茹上传、下载、播放、获取文件路径
    //gpfIndex(1.代表上传  2.代表下载 3.代表音乐播放 4.代表视频播放 5.代表获取文件路径和打开文件 6.图片浏览)
    public synchronized native boolean gsGPFConnect(int gpfIndex);

    public synchronized native boolean gsClearAndReconnectGPF(int gpfIndex);

    //判断对应gpfIndex的gpf是否是可用的
    public native boolean gsIsGPFok(int gpfIndex);

    //获取指定路径的文件ID
    public synchronized native String gsGPathFileId(int gpfIndex, String Id, int sortType, int offset, int count);

    //刷新指定目录文件
    public synchronized native String gsGetPathFileRefresh(int gpfIndex, String Id, int sortType, int offset, int count);

    //返回首页图片音乐视频等模块的数量信息
    public synchronized native String gsGetCounts(int gpfIndex);


    //
    //函数名：gsPutFileId
    //功能：下载指定的本地文件到“云存储服务器上”指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：返回 0:上传成功 其他返回值代表失败
    //
    //public native int gsPutFileId(int gpfIndex, String local, String dirId, String remote);

    //public native int gsPutFileId2(int gpfIndex, String local, String dirId, String remote);

    public native String gsPutFileIdRetId(int gpfIndex, String local, String dirId, String remote);
    public native String gsPutFileIdRetId2(int gpfIndex, String local, String dirId, String remote);

    //
    //函数名：gsGetFileId
    //功能：下载指定的文件到本地指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：为0时成功，为1表明正在进行，<0表明失败
    //
    public native int gsGetFileId(int gpfIndex, String Id, String local);

    public native int gsGetFileId2(int gpfIndex, String Id, String local);

    public synchronized native int gsAddDirID(int gpfIndex, String parentDirId, String dirName, byte[] newDirId);

    public synchronized native int gsRenameDirID(int gpfIndex, String dirId, String destName);

    public synchronized native int gsRenameFileID(int gpfIndex, String dirId, String destName);

    public synchronized native int gsDeleteDirID(int gpfIndex, String Id);

    public synchronized native int gsGetFileToBuffer(int gpfIndex, String id, long offset, long count, byte[] buffer, long[] leng);

    public synchronized native int gsGetFileToBufferId(int gpfIndex, String id, long offset, long count, byte[] buffer, long[] leng);
    /**
     * 获取内网ip
     *
     * @return
     */
    public synchronized native int gsGetPeerLocalIp();

    /**
     * 获取外网ip
     *
     * @return
     */
    public native int gsGetPeerInternetIp();

    /**
     * 获取容量
     *
     * @return
     */
    public synchronized native int gsGetCapacity(int index, long[] total, long[] free);

    /**
     * GHANDLE handle, FILEINFO_TYPE filetype, GETINFOSORT_TYPE sorttype, uint32_t offset, uint32_t count
     *
     * @return
     */
    public synchronized native String gsGetFileTypeInfo(int index, int fileType, int sortType, int offset, int count);

    /**
     * 删除文件
     *
     * @param gpfIndex
     * @param destName
     * @return
     */
    public synchronized native int gsDeleteFileID(int gpfIndex, String destName);

    /**
     * 全盘搜索
     * char *GPFSearchFile(GHANDLE handle, FILEINFO_TYPE filetype, GETINFOSORT_TYPE sorttype, const char *search, uint32_t offset, uint32_t count);
     *
     * @return
     */
    public synchronized native String gsSearch(int gpfIndex, int fileType, int sortType, String searchTxt, int pageNo, int pageSize);
//
//    /**
//     * (JNIEnv *, jobject,jint,jstring,jint,jstring);
//     *
//     * @return
//     */
//    public synchronized native int gsGetThunbNail(int gpfIndex, String uuid, int thumType, String path);

    /**
     * int GPFUpdateRelFile(GHANDLE  handle, const char *fileid, FILEINFO_TYPE filetype, FILE_UPDATETYPE updatetype, uint32_t subtype,
     * const uint8_t* buff, uint32_t bufflen);
     * JNIEnv *, jobject,jint,jstring,jint,jint,jint,jbyteArray,jint);
     * /// \param handle GPFConnect返回的handle
     * /// \param fileid 对应某个文件的id。
     * /// \param filetype代表上传类型：目前支持图片，文档，声音
     * /// \param updatetype代表类型，这个函数目前仅仅支持FILE_REPLACE操作，稍后会增加FILE_ADD操作
     * /// \param buff 上传数据缓存
     * /// \param bufflen 上传数据大小
     * /// \param subtype 子类型
     * ///                如果是图片的话，这个类型为TBFILE_TYPE thumType
     * ///                其他这个值暂时没有意义
     * /// \return 大于等于0代表成功，其他的值请参看GOONAS_ERRCODE
     *
     * @return
     */
    public synchronized native int gsUpdatePIV(int gpfIndex, String uuid, int fileType, int updateType, int thumType, byte[] data, int dataLen);
    public synchronized native int gsUpdatePIVpath(int gpfIndex, String uuid, int fileType, int updateType, int thumType, String Thumbpath);

    /**
     * JNIEXPORT jint JNICALL Java_com_ipeercloud_com_controler_GsSocketManager_gsGetThunbNail
     * (JNIEnv *, jobject,jint,jstring,jint,jstring);
     * <p>
     * int GPFGetThumbnail(GHANDLE handle, const char *remotefileid, TBFILE_TYPE tbtype, uint8_t **filecontent, uint32_t *filelen);
     * /// \param handle GPFConnect返回的handle
     * /// \param remotefileid 文件id
     * /// \param tbtype 类型，请参看TBFILE_TYPE
     * /// \param filecontent 文件对应缓存
     * /// \param filelen 文件对应大小
     * /// \return 返回值，为0时成功，否则为失败
     *
     * @param gpfIndex
     * @return
     */
    public synchronized native int gsGetThunbNail(int gpfIndex, String uuid, int thumType, String filePath);


    /**
     * 获取文字
     *
     * @param gpfIndex
     * @param uuid
     * @param path
     * @return
     */
    public synchronized native int gsGetTextDesc(int gpfIndex, String uuid, String path);

    /**
     * 获取语音
     *
     * @param gpfIndex
     * @param uuid
     * @param path
     * @return
     */
    public synchronized native int gsGetVoice(int gpfIndex, String uuid, String path);

    /**
     * @param gpfIndex
     * @param path     头像的绝对路径
     * @return
     */
    public synchronized native int gsGetSelfPic(int gpfIndex, String path);

    /**
     * @param gpfIndex
     * @param path     上传头像
     * @return
     */
    public synchronized native int gsPutSelfPic(int gpfIndex, String path);
//    JNIEXPORT jint JNICALL Java_com_ipeercloud_com_controler_GsSocketManager_gsChangePasswordId
//            (JNIEnv *, jobject,jstring,jint,jstring,jstring,jstring);

    public synchronized native int gsChangePasswordId(String id, int port, String useNam, String oldPassword, String newPassword);

    //JNIEXPORT jint JNICALL Java_com_ipeercloud_com_controler_GsSocketManager_gsMoveFile
//        (JNIEnv *, jobject,jint,jstring,jstring);
    public synchronized native int gsMoveFile(int gdfIndex, String sourceId, String destId);


    /**
     * 绑定邮箱
     *
     * @param serverIp
     * @param port
     * @param newEmail
     * @param newPassword
     * @param oldAccount
     * @param oldPassword
     * @return
     */
    public synchronized native int gsBindEmail(String serverIp, int port, String newEmail, String newPassword, String oldAccount, String oldPassword);

    /**
     * 重置密码
     *
     * @param serverIp
     * @param port
     * @param email
     * @return
     */
    public synchronized native int gsResetPwd(String serverIp, int port, String email);
    //add 2018年6月4日20:01:39

    /**
     *设置当前状态
     * @param gpfIndex
     * @param password
     * @param set
     * @return
     */
    public synchronized native int  gsGPFSetPathPassword(int gpfIndex,String password,int set);

    /**
     * 文件夹加密
     * @param gpfIndex
     * @param pathId
     * @param set
     * @return
     */
    public synchronized native int GPFEncryptionPathId(int gpfIndex,String pathId,int set);

    /**
     * 获取用户的权限状态
     * @param gpfIndex
     * @param arr
     * @return
     */
    public synchronized native int gsGPFGetPathPasswordStatus(int gpfIndex,int[] arr);

    /**
     *文件夹加密： 设置密码，重置密码
     * @param gpf
     * @param oldPassword
     * @param newPassword
     * @param reset
     * @return
     */
    public synchronized native int  gsGPFUpdatePathPassword(int gpf,String oldPassword,String newPassword,int reset);

    /**
     * 忘记密码的时候调用，删除密码。需要调用loginAccount 成功后才调用该接口
     * @param gpfIndex
     * @param password
     * @return
     */

    public synchronized native int gsGPFDeletePathPassword(int gpfIndex,String password);
    /// \brief 解绑用户id对应的设备id
    /// \param serverip 服务器ip
    /// \param port 服务器port
    /// \param userid 用户id（目前仅仅支持邮件id）
    /// \param password 用户密码
    /// \param deviceid 设备id
    /// \param [out]tempid 解绑对应的临时id , 设置的密码为000000
    /// \return 返回值，0代表成功，其他代表失败
//    int GUnbindDevicieId(const char *serverip, uint16_t port,
//        const char *userid, const char *password, int64_t deviceid, char **tempid);
    public synchronized native int gsGUnbindDevicieId(String ip,int port,String phoneOrEmail,String password,long deviceId,byte[] tempId);


    //----------old api---------
    //
    //函数名：helloGoonas
    //功能：返回当前jni库的版本，确定java调用goonas jni库已经成功
    //输入参数：无
    //返回数据：返回当前jni库的版本信息
    //
   // public native String helloGoonas();

    //
    //函数名：gsUserRegister
    //功能：注册一个新的用户账户
    //输入参数：server是goonas的域名，根据手机语言中国用sz.goonas.com，其他国家用utah.goonas.com，
    // 			user表示要注册的账户名（有效的邮箱地址），password表示账户的密码（6个字符以上）
    //返回数据：返回 true表示成功，false表示失败
    //
   // public native boolean gsUserRegister(String server, String user, String passowrd);

    //
    //函数名：gsChangePassword
    //功能：改变账户密码，
    //输入参数：user表示当前账户名，oldpassword表示旧的密码，newpassword表示新的密码
    //返回数据：返回 true表示成功，false表示失败
    //
    //public native boolean gsChangePassword(String user, String oldpassword, String newpassword);

    //
    //函数名：gsResetPassword
    //功能：重置账户密码，goonas会发送重置密码的链接到账户邮箱中，点击后即可重置，用邮箱中的临时密码登录后修改密码。
    //输入参数：server表示goonas的域名，user表示当前要重置密码的账户
    //返回数据：返回 true表示成功，false表示失败
    //
   // public native boolean gsResetPassword(String server, String user);

    //
    //函数名：gsLogin
    //功能：登录goonas服务系统，使用后续文件服务必须要先登录goonas
    //输入参数：server表示goonas的域名，user表示当前的账户，password表示当前的账户密码
    //返回数据：返回 true表示成功，false表示失败
    //
    //public synchronized native boolean gsLogin(String server, String user, String password);

    //
    //函数名：gsLinked
    //功能：返回当前账户是否绑定了一台私有云存储服务器终端
    //输入参数：无
    //返回数据：返回 true表示已经绑定，false表示失败
    //
    //public native boolean gsLinked();

    //
    //函数名：gsOnline
    //功能：返回当前账户绑定的私有云存储服务器终端是否在线
    //输入参数：无
    //返回数据：返回 true表示在线，false表示失败
    //
    public native boolean gsOnline();

    //
    //函数名：gsLinkCloudServer
    //功能：绑定一台私有云存储服务器终端到当前的账号
    //输入参数：私有云存储服务器的UUID号，UUID号可以在产品的外包装上找到
    //返回数据：返回 true表示成功，false表示失败
    //
   // public native boolean gsLinkCloudServer(String CloudServerUuid);

    //
    //函数名：gsGetPathFile
    //功能：获取指定目录下的所有文件名和目录名等参数
    //输入参数：准备获取数据的目录完全路径名，根目录是“\"
    //返回数据：以json格式返回数据
    //	/// json格式：
    /*
    [
        {
                "FileName": "$360Section",      // 名称
                "FileSize": 0,                  // 文件大小
                "FileType": 0,                  // 类型：对应PATHFILE_TYPE
				"LastModifyTime": 1464752412    // 最后修改时间
        },
        {
                "FileName": "$Recycle.Bin",
                "FileSize": 0,
                "FileType": 0,
				"LastModifyTime": 1464752412
        },
        {
                "FileName": "转账单.xlsx",
                "FileSize": 11842,
                "FileType": 1,
				"LastModifyTime": 1464752412
        }
	]
	*/
   // public synchronized native String gsGetPathFile(String path);

    //
    //函数名：gsReturnConnectedMode
    //功能：返回当前跟“云服务器”的通信连接模式
    //输入参数：无
    //返回数据：返回 1:为直连模式 2:中转模式 3:局域网LAN模式
    //

   // public native int gsReturnConnectedMode();

    //
    //函数名：gsPutFile
    //功能：下载指定的本地文件到“云存储服务器上”指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：返回 0:上传成功 其他返回值代表失败
    //
    //public synchronized native int gsPutFile(String local, String remote);

    //
    //函数名：gsGetFile
    //功能：下载指定的文件到本地指定的路径
    //输入参数：remote是"云服务器“上的从根路径开始的完整路径，local是要保存到本地的完整路径
    //返回数据：为0时成功，为1表明正在进行，<0表明失败
    //
    //public synchronized native int gsGetFile(String remote, String local);

    //
    //函数名：gsFreePathFile
    //功能：释放gsGetPathFile占用的资源
    //输入参数：无
    //返回数据：返回 无s
    //
    //public native void gsFreePathFile();

    //
    //函数名：gsAddWifi
    //功能：为设备增加一组WIFI连接
    //输入参数：wifiName是wifi的名称，密码
    //返回数据：返回逻辑成功与失败
    //
    //public native boolean gsAddWifi(String wifiName, String password);

    //
    //函数名：gsReadFileBuffer
    //功能：读取指定的数据到Buffer缓冲区
    //输入参数：remote要读取的远端云存储服务上的文件完全路径（文件名及它的路径），offset指定要从哪个位置开始读取，count指定要读取的字节数，buf指定读取
    //			到哪里，即缓冲区，bufflen返回实际读取到的数量。
    //			bufflen读取数据缓存大小，这个值在函数前需要填写buff的大小，完成会修改成为真实读取的大小。
    //返回数据：返回值，为0时成功，为1表明已经到达结尾，没有数据可读，小于0代表失败,
    //
    public synchronized native int gsReadFileBuffer(String remote, long offset, int count, byte[] buffer, int[] bufflen);

    //
    //函数名：gsGetSpace
    //功能：读取云盘的容量
    //输入参数：传递一个long的buff接收返回的uint64_t类型值
    //			total[]会返回云盘的总容量
    //			free[]会返回云盘的空闲容量
    //返回数据：返回逻辑成功与失败,成功返回容量值到total和free两个buf
    //
   // public native boolean gsGetSpace(long[] total, long[] free);

    //
    //函数名：gsGetLocalSDKVer
    //功能：读取app SDK的版本号
    //输入参数：无
    //返回数据：返回字符串，本地SDK的版本号
    //
    public native String gsGetLocalSDKVerV2(int args);

    //
    //函数名：gsGetPeerSDKVer
    //功能：读取云盘使用的SDK的版本号
    //输入参数：无
    //返回数据：返回字符串，云盘SDK的版本号
    //
    //public native String gsGetPeerSDKVer();

    //
    //函数名：gsGetPeerSDKVer
    //功能：读取云盘使用的SDK的版本号
    //输入参数：无
    //返回数据：返回字符串，云盘SDK的版本号
    public native String gsGetPeerSDKVerV2(int index);
    //public native String gsGetPeerSDKVerV3(int index);
    /**
     * 重置用户账号信息
     * @param userName
     * @param password
     * @return
     */
    public native int gsResetUserNameAndPassword(String userName,String password);

    //
    //函数名：gsPing
    //功能：探测互联网是否通畅
    //输入参数：domainip是字符串，点分ip地址
    //返回数据：<0时表示网络不通，异常；>0时是网络的延时值
    //
    //public native int gsPing(String domainip);


    //
    //函数名：gsNewDir
    //功能：新建目录
    //输入参数：新建目录的路径
    //返回数据：返回逻辑成功与失败
    //
    //public native boolean gsNewDir(String path);


    //
    //函数名：gsRemoveDir
    //功能：删除目录
    //输入参数：新建目录的路径
    //返回数据：返回逻辑成功与失败
    //
    //public native boolean gsRemoveDir(String path);


    //
    //函数名：gsRemoveFile
    //功能：删除文件
    //输入参数：目标文件的路径和文件名
    //返回数据：返回逻辑成功与失败
    //
    //public native boolean gsRemoveFile(String path);


    //
    //函数名：gsRnameFile
    //功能：文件重命令
    //输入参数：老文件名src,新文件名dest
    //返回数据：返回逻辑成功与失败
    //
   // public native boolean gsRenameFile(String src, String dest);

    //
    //函数名：gsRnameDir
    //功能：文件夹重命令
    //输入参数：老文件夹名src,新文件夹名dest
    //返回数据：返回逻辑成功与失败
    //
   // public native boolean gsRenameDir(String src, String dest);

    //
    //函数名：gsCloudLanIp
    //功能：返回当前云盘的局域网ip地址
    //输入参数：无
    //返回数据：返回当前云盘的局域网ip地址
    //
   // public native String gsCloudLanIp();

    //
    //函数名：gsCloudWanIp
    //功能：返回当前云盘的公网ip地址
    //输入参数：无
    //返回数据：返回当前云盘的公网ip地址
    //
   // public native String gsCloudWanIp();

    public synchronized native void gsCloseHandle();


    public static void main(String[] args) {
        return;
    }



}
