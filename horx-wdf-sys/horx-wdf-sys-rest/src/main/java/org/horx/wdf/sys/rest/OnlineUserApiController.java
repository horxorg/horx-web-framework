package org.horx.wdf.sys.rest;

import org.horx.wdf.common.entity.PagingParam;
import org.horx.wdf.common.entity.PagingQuery;
import org.horx.wdf.common.entity.PagingResult;
import org.horx.wdf.common.entity.Result;
import org.horx.wdf.common.arg.annotation.ArgEntity;
import org.horx.wdf.sys.annotation.AccessPermission;
import org.horx.wdf.sys.annotation.ArgDataAuth;
import org.horx.wdf.sys.converter.query.OnlineUserQueryVoConverter;
import org.horx.wdf.sys.dto.OnlineUserDTO;
import org.horx.wdf.sys.dto.dataauth.SysDataAuthDTO;
import org.horx.wdf.sys.dto.query.OnlineUserQueryDTO;
import org.horx.wdf.sys.dto.wrapper.BatchWithSysAuthDTO;
import org.horx.wdf.sys.service.OnlineUserService;
import org.horx.wdf.sys.vo.query.OnlineUserQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线用户API控制器。
 * @since 1.0
 */
@RestController
@RequestMapping("/api/sys/onlineUser")
public class OnlineUserApiController {

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private OnlineUserQueryVoConverter onlineUserQueryVoConverter;

    @Autowired
    private SessionRepository sessionRepository;

    @AccessPermission("sys.onlineUser.query")
    @PostMapping("/pagingQuery")
    public PagingResult<OnlineUserDTO> pagingQuery(@ArgEntity OnlineUserQueryVO query, PagingParam pagingParam) {
        OnlineUserQueryDTO onlineUserQueryDTO = onlineUserQueryVoConverter.fromVo(query);
        PagingQuery<OnlineUserQueryDTO> pagingQuery = new PagingQuery<>(onlineUserQueryDTO, pagingParam);
        PagingResult<OnlineUserDTO> pagingResult = onlineUserService.pagingQuery(pagingQuery);
        return pagingResult;
    }

    @AccessPermission("sys.onlineUser.offline")
    @PostMapping("/offline")
    public Result offline(@RequestParam("id[]") Long[] ids, @ArgDataAuth SysDataAuthDTO sysDataAuth) {
        BatchWithSysAuthDTO batchWithAuthDTO = new BatchWithSysAuthDTO();
        batchWithAuthDTO.setIds(ids);
        batchWithAuthDTO.setSysDataAuth(sysDataAuth);
        String[] sessionKeys = onlineUserService.offlineCheck(batchWithAuthDTO);
        if (sessionKeys != null && sessionKeys.length > 0) {
            for (String sessionKey : sessionKeys) {
                sessionRepository.delete(sessionKey);
            }
        }
        return new Result();
    }
}
