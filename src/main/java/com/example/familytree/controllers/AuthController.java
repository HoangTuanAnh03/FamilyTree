package com.example.familytree.controllers;

import com.example.familytree.entities.KeyTokenEntity;
import com.example.familytree.entities.UserAccountEntity;
import com.example.familytree.models.ApiResult;
import com.example.familytree.models.dto.AuthRequest;
import com.example.familytree.models.dto.LoginResponse;
import com.example.familytree.models.dto.TokenResponse;
import com.example.familytree.models.dto.UserInfo;
import com.example.familytree.repositories.KeyRepository;
import com.example.familytree.repositories.UserAccountRepository;
import com.example.familytree.security.JwtService;
import com.example.familytree.services.UserAccountService;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@RestController
@RequiredArgsConstructor
public class AuthController {


    private final JwtService jwtService;
    private final UserAccountService userAccountService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final KeyRepository keyRepository;

    @GetMapping(path = "/test")
    // [GET] localhost:8080/admins/list
    public String test() {
        return "uuuuuu";
    }

    @GetMapping("/verifyRefreshToken")
    public ResponseEntity<ApiResult<TokenResponse>> verifyUser(@RequestParam(name = "token") String token) {
        ApiResult<TokenResponse> result = null;


        String username = null;
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();

        username = jsonObject.get("sub").getAsString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserAccountEntity user = userAccountRepository.findFirstByUserEmail(username);
        KeyTokenEntity keyByUser = keyRepository.findFirstByUserId(user.getUserId());
        try {
            if (jwtService.validateToken(token, keyByUser.getPrivateKey(), userDetails)) {
                // Tạo lại AccessToken và RefreshToken
                String refreshToken = jwtService.generateRefreshToken(user.getUserEmail(), keyByUser.getPrivateKey());
                TokenResponse tokens = TokenResponse.create(
                        jwtService.generateAccessToken(user.getUserEmail(), keyByUser.getPublicKey()),
                        refreshToken
                );
                // Lưu lại refreshToken vào DB
                /*
                 *
                 *
                 * */
                keyByUser.setRefreshToken(refreshToken);
                keyRepository.save(keyByUser);


                result = ApiResult.create(HttpStatus.OK, "Cấp lại AccessToken và RefreshToken thành công!!", tokens);
            }
        } catch (Exception ex) {
            result = ApiResult.create(HttpStatus.OK, "RefreshToken sai!", null);

        }
        return ResponseEntity.ok(result);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<ApiResult<LoginResponse>> login(@RequestBody AuthRequest authRequest) {

        ApiResult<LoginResponse> result = null;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                UserAccountEntity user = userAccountRepository.findFirstByUserEmail(authRequest.getEmail());
                if (user != null && user.getUserStatus() ) {
                    // lấy key ra
                    KeyTokenEntity keyByUser = keyRepository.findFirstByUserId(user.getUserId());
                    UserInfo userInfo = UserInfo.create(
                            user.getUserId(),
                            user.getUserEmail(),
                            user.getUserFullname()
                    );

                    String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail(), keyByUser.getPrivateKey());

                    TokenResponse tokens = TokenResponse.create(
                            jwtService.generateAccessToken(authRequest.getEmail(), keyByUser.getPublicKey()),
                            refreshToken
                    );

                    LoginResponse loginResponse = LoginResponse.create(
                            userInfo,
                            tokens
                    );
                    result = ApiResult.create(HttpStatus.OK, "Đăng nhập thành công!!", loginResponse);
                    // Lưu refreshToken vào db
                    keyByUser.setRefreshToken(refreshToken);
                    keyRepository.save(keyByUser);

                    return ResponseEntity.ok(result);
                }
                result = ApiResult.create(HttpStatus.OK, "tài khoản chưa được kích hoạt!!", null);
            }
        } catch (Exception ex){
            result = ApiResult.create(HttpStatus.OK, "Sai tên đăng nhập hoặc mật khẩu!!", null);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(result);
    }

}
