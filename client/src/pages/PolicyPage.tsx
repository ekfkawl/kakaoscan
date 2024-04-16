import React from 'react';

const PolicyPage = () => {
    return (
        <div className="mx-auto max-w-screen-lg">
            <h3 className="mb-8 text-3xl font-extrabold tracking-tight text-gray-900 dark:text-white">포인트</h3>
            <div className="grid border-t border-gray-200 pt-8 text-left dark:border-gray-700 md:grid-cols-2 md:gap-16">
                <div className="break-all">
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            특정 대상을 최근 24시간 이내 처음 조회
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            특정 대상을 최근 24시간 이내 처음 조회하시는 경우 <strong>1,000P</strong>가 차감됩니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            이전에 조회한 적이 있는 대상을 24시간 이내 다시 조회
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            이전에 1,000P가 차감 된 시점으로부터 24시간 동안은 해당 대상의 조회 비용이 <strong>50%</strong> 할인됩니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            조회 비용 지불 시점으로부터 10분 이내 대상을 다시 조회
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            이전에 포인트가 차감 된 시점으로부터 10분 동안은 해당 대상의 조회 비용이 <strong>무료</strong>입니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            프로필 조회에 성공하는 경우에만 포인트 차감
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            대상이 카카오톡에 가입되지 않았거나 "전화번호로 친구 추가 허용" 옵션이 비활성화 상태면
                            조회가 불가능합니다. 또는 본 서비스의 일시적인 오류로 조회에 실패할 수 있습니다. 이러한
                            경우에는 조회에 실패하여도 포인트가 차감되지 않습니다.
                        </p>
                    </div>
                </div>
                <div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            포인트 충전
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            포인트 충전(구매)은 현재 계좌이체로만 가능합니다. 반드시 입금자명을 지켜주셔야 결제가 정상
                            처리됩니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            포인트 환불
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            대상이 카카오톡에 가입되지 않았거나 "전화번호로 친구 추가 허용" 옵션이 비활성화 상태면
                            조회가 불가능한 경우, 또는 본 서비스의 오류 지속으로 조회가 불가능한 경우에는 환불 문의
                            채팅을 남겨주시면 확인 후 포인트 잔액 만큼 환불해드립니다.
                        </p>
                    </div>
                </div>
            </div>

            <h3 className="mt-3 mb-8 text-3xl font-extrabold tracking-tight text-gray-900 dark:text-white">
                프로필 조회
            </h3>
            <div className="grid border-t border-gray-200 pt-8 text-left dark:border-gray-700 md:grid-cols-2 md:gap-16">
                <div className="break-all">
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            신규번호 일일 조회 가능 횟수
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            이전에 조회 한 적 없던 신규번호 프로필 조회는 일일 최대 5번 가능합니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            대상의 프로필과 배경이 100장을 초과하는 경우
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            대상의 프로필과 배경은 각각 최대 100장까지만 불러옵니다. 가끔 일시적인 조회 오류로 인해
                            약간의 오차가 발생할 수 있습니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            프로필 조회 진행 중 서버 연결에 실패 메세지
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            프로필 조회 진행 중 간혹 '서버 연결에 실패하였습니다' 라는 오류 메세지를 응답 받을 수
                            있습니다. 이는 포인트가 차감되지 않으며 30초~1분 정도 후에 재시도 하시면 됩니다.
                        </p>
                    </div>
                </div>
                <div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            프로필 조회 시점 스냅샷 보관
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            프로필 조회 성공 시, 해당 시점의 스냅샷이 조회 내역에 48시간 동안 보관됩니다.
                        </p>
                    </div>
                    <div className="mb-10">
                        <h3 className="mb-4 flex items-center text-lg font-medium text-gray-900 dark:text-white">
                            프로필 조회 시작 후 사이트 이탈
                        </h3>
                        <p className="text-gray-500 dark:text-gray-400">
                            프로필 조회 시작 후 사이트를 이탈해도 프로필 조회는 계속 진행되며 조회 완료 전에 재입장 시
                            이어서 진행됩니다. 재입장을 하지 않더라도 자동으로 조회 내역에 저장됩니다.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PolicyPage;
